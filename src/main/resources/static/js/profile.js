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

loadProfileData();
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
    })
}

function getTasksProfile() {
    apiGet.get('http://localhost:8080/task/getTasks/').then(res => {
        tBody.innerHTML = "";
        res.data.forEach(f => {
            console.log(f.taskName)
            addRowProfile(f.taskName)
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
    })
}

function updateProfile() {
    const api = axios.create({
        withCredentials: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
            "Content-Type": "application/json"
        }
    });

    api.put("http://localhost:8080/user/update/", {
        username: inpUsername.value,
        email: inpEmail.value,
        profileImgName: profileImage.alt,
    });
}

function changeTaskArchive(taskName) {
    const apiPut = axios.create({
        withCredentials: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
            "Content-Type": "text/plain"
        }
    });

    apiPut.put("http://localhost:8080/task/changeTaskArchive/",
        taskName).then(res => getTasksProfile());
}

function deleteTask(taskName) {
    const apiDelete = axios.create({
        withCredentials: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
        }
    });
    apiDelete.delete("http://localhost:8080/task/" + taskName)
        .then(res => getTasksProfile())
}


function loadProfileData() {
    if (checkboxEdit.checked === true) {
        for (let item of formInput) {
            iconButton.classList.remove('disableButtonHover')
            iconButton.disabled = false;
            item.classList.remove('fromInputUnEditable')
            item.removeAttribute('disabled');
            item.classList.add('fromInputEditable')
            buttonSaveChanges.style.display = 'inline-block'
        }
    } else {
        for (let item of formInput) {
            iconButton.disabled = true;
            iconButton.classList.add('disableButtonHover')
            item.classList.remove('fromInputEditable')
            item.classList.add('fromInputUnEditable')
            item.setAttribute('disabled', 'disabled');
            buttonSaveChanges.style.display = 'none'
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