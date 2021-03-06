let checkboxEdit = document.getElementById('checkboxEdit');
let formInput = document.getElementsByClassName('formInput');
let buttonSaveChanges = document.getElementById('buttonSaveChanges');
let iconList = document.getElementById('iconList');
let changeIconWindow = document.getElementById('changeIconWindow');
let profileImage = document.getElementById('profileImage');
let iconButton = document.getElementById('iconButton');
let inpUsername = document.getElementById('inpUsername');
let inpEmail = document.getElementById('inpEmail');
let labelPublicId = document.getElementById('labelPublicId');
let tBody = document.getElementById('tBody');

const apiGet = axios.create({
    withCredentials: true
});

const apiTextPlain = axios.create({
    withCredentials: true,
    headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "text/plain"
    }
});

editFrom();
getIconList();
getUser();
getTasksProfile();

function getUser() {
    apiGet.get('http://localhost:8080/user/get/').then(res => {
        labelPublicId.innerHTML = "" + res.data.publicId;
        inpUsername.setAttribute('value', res.data.username);
        inpEmail.setAttribute('value', res.data.email);
        profileImage.setAttribute('alt', res.data.profileImg.name);
        profileImage.src = res.data.profileImg.urlAddress;
    }).catch(error => {
        alert(error.response.data.message)
    })
}

function getTasksProfile() {
    apiGet.get('http://localhost:8080/task/getTasks/').then(res => {
        tBody.innerHTML = "";
        res.data.forEach(f => {
            addRowProfile(f.taskName)
        }).catch(error => {
            alert(error.response.data.message)
        })
    })
}

function getIconList() {
    apiGet.get('http://localhost:8080/profileImg/getAll/').then(res => {
        res.data.forEach(f => {
            let tmp = new Image();
            tmp.setAttribute('onclick', 'setNewIcon();');
            tmp.setAttribute('alt', f.name);
            tmp.src = f.urlAddress;
            iconList.appendChild(tmp);
        })
    }).catch(error => {
        alert(error.response.data.message)
    });
}

function changePassword() {
apiTextPlain.post("http://localhost:8080/user/changePasswordTokenRequest/",
    inpEmail.value
    ).catch(error => {
    alert(error.response.data.message)
});
}

function updateProfile() {
const apiPost = axios.create({
    withCredentials: true,
    headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "application/json"
    }
});

apiPost.put("http://localhost:8080/user/update/", {
    username: inpUsername.value,
    email: inpEmail.value,
    profileImgName: profileImage.alt,
}).catch(error => {
    alert(error.response.data.message)
});
}

function changeTaskArchive(taskName) {
    apiTextPlain.put("http://localhost:8080/task/changeTaskArchive/",
        taskName)
        .then(() => getTasksProfile())
        .catch(error => {
            alert(error.response.data.message)
        });
}

function postTask(taskName) {
    apiTextPlain.post("http://localhost:8080/task/",
        taskName)
        .then(() => getTasksProfile())
        .catch(error => {
            alert(error.response.data.message)
        });
}

function deleteTask(taskName) {
    const apiDelete = axios.create({
        withCredentials: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
        }
    });
    apiDelete.delete("http://localhost:8080/task/" + taskName)
        .then(() => getTasksProfile())
        .catch(error => {
            alert(error.response.data.message)
        })
}

function createTask() {
    postTask(document.getElementById('createTask').value);
    document.getElementById('createTask').value = "";
}

function editFrom() {
    if (checkboxEdit.checked === true) {
        for (let item of formInput) {
            iconButton.classList.remove('disableButtonHover')
            iconButton.disabled = false;
            item.classList.remove('fromInputUnEditable')
            item.removeAttribute('disabled');
            item.classList.add('fromInputEditable')
            buttonSaveChanges.style.display = 'block'
            buttonChangePassword.style.display = 'block'
        }
    } else {
        for (let item of formInput) {
            iconButton.disabled = true;
            iconButton.classList.add('disableButtonHover')
            item.classList.remove('fromInputEditable')
            item.classList.add('fromInputUnEditable')
            item.setAttribute('disabled', 'disabled');
            buttonSaveChanges.style.display = 'none'
            buttonChangePassword.style.display = 'none'
        }
    }
}

function setNewIcon() {
    document.addEventListener('click', res => {
        profileImage.src = res.target.src;
        profileImage.alt = res.target.alt;
        changeIconWindow.classList.add('isHidden');
    }, {
        once: true
    });
}

function addRowProfile(taskName) {
    let newRow = tBody.insertRow(-1);

    let taskNameCell = newRow.insertCell(0);
    let archiveCell = newRow.insertCell(1);
    let deleteCell = newRow.insertCell(2);

    let taskNameText = document.createTextNode(taskName);

    taskNameCell.appendChild(taskNameText);
    archiveCell.appendChild(createArchiveButton(taskName))
    deleteCell.appendChild(createDeleteButton(taskName))
}


function createArchiveButton(taskName) {
    let buttonArchive = document.createElement("button");
    buttonArchive.classList.add("archive")
    buttonArchive.innerHTML = "<i class=\"fas fa-archive\"></i>"
    buttonArchive.setAttribute("onclick", "archiveAlert(" + "\"" + taskName + "\"" + ")")
    return buttonArchive;
}

function createDeleteButton(taskName) {
    let buttonArchive = document.createElement("button");
    buttonArchive.classList.add("delete")
    buttonArchive.innerHTML = "<i class=\"fas fa-trash\"></i>"
    buttonArchive.setAttribute("onclick", "deleteAlert(" + "\"" + taskName + "\"" + ")")
    return buttonArchive;
}

function archiveAlert(taskName) {
    let tmp = confirm("Are you sure to archive task?");
    if (tmp === true) {
        changeTaskArchive(taskName);
    } else {
    }
}

function deleteAlert(taskName) {
    let tmp = confirm("Are you sure to delete task?");
    if (tmp === true) {
        deleteTask(taskName);
    } else {
    }
}

function displayIconList() {
    changeIconWindow.classList.remove('isHidden');
}