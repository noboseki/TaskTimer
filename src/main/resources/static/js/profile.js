let checkboxEdit = document.getElementById('checkboxEdit');
let formInput = document.getElementsByClassName('formInput');
let buttonSaveChanges = document.getElementById('button-save-changes');
let iconList = document.getElementById('icon-list');
let changeIconWindow = document.getElementById('change-icon-window');
let profileImage = document.getElementById('profile-image');
let iconButton = document.getElementById('icon-button');
let inpUsername = document.getElementById('inpUsername');
let inpEmail = document.getElementById('inpEmail');
let labelPublicId = document.getElementById('labelPublicId');

editFrom();
getIconList();
getUser();

function getUser() {
    const api = axios.create({
        withCredentials: true
    });

    api.get('http://localhost:8080/user/get/').then(res => {
        inpUsername.setAttribute('value', res.data.username);
        inpEmail.setAttribute('value', res.data.email);
        profileImage.setAttribute('alt', res.data.profileImg.name);
        labelPublicId.innerHTML = "" + res.data.publicId;
        profileImage.src = res.data.profileImg.urlAddress;
        res.data.taskList.forEach(f => addRow(f.taskName, f.time, f.complete));
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

     axios.put("http://localhost:8080/user/update/", {
        username: inpUsername.value,
        email:  inpEmail.value,
        profileImgName: profileImage.alt,
     });
}

function getIconList() {
    const api = axios.create({
        withCredentials: true
    });

    api.get('http://localhost:8080/profileImg/getAll/').then(res => {
        res.data.forEach(f => {
            let tmp = new Image();
            tmp.setAttribute('onclick', 'setNewIcon();');
            tmp.setAttribute('alt', f.name);
            tmp.src = f.urlAddress;
            iconList.appendChild(tmp);
        })
    })
}

function editFrom() {
    if (checkboxEdit.checked == true) {
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

function displayIconList() {
    changeIconWindow.classList.remove('isHidden');
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

function addRow(taskName, time, complete) {
    let tBodyRef = document.getElementById('tBody');

    let newRow = tBodyRef.insertRow(-1);

    let taskNameCell = newRow.insertCell(0);
    let timeCell = newRow.insertCell(1);
    let completeCell = newRow.insertCell(2);

    let taskNameText = document.createTextNode(taskName);
    let timeText = document.createTextNode(time);
    let completeText = document.createTextNode(complete);

    taskNameCell.appendChild(taskNameText);
    timeCell.appendChild(timeText);
    completeCell.appendChild(completeText);
}