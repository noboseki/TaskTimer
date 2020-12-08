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
const apiGet = axios.create({
    withCredentials: true
});

editFrom();
getIconList();
getUser();

function getUser() {
    apiGet.get('http://localhost:8080/user/get/').then(res => {
        labelPublicId.innerHTML = "" + res.data.publicId;
        inpUsername.setAttribute('value', res.data.username);
        inpEmail.setAttribute('value', res.data.email);
        profileImage.setAttribute('alt', res.data.profileImg.name);
        profileImage.src = res.data.profileImg.urlAddress;
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

function editFrom() {
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

