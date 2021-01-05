let oldPassword = document.getElementById('oldPassword')
let newPassword = document.getElementById('newPassword')
let repeatPassword = document.getElementById('repeatPassword')



function changePassword(){
    infoTable.innerHTML = '';
    infoTable.style.display = 'none';

    if (checkRepeatPassword() && checkPasswordLenght() && inputCorrectSymbols(newPassword)) {
        newPassword.style.backgroundColor = 'rgba(0, 151, 0, 0.6)';
        repeatPassword.style.backgroundColor = 'rgba(0, 151, 0, 0.6)';
        changePasswordRequest();
    } else {
        infoTable.style.display = 'block';
        infoTable.style.backgroundColor = 'rgba(255, 0, 0, 0.6)';
        newPassword.style.backgroundColor = 'rgba(255, 0, 0, 0.6)';
        repeatPassword.style.backgroundColor = 'rgba(255, 0, 0, 0.6)';
    }
}

function changePasswordRequest() {
    const api = axios.create({
        withCredentials: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
            "Content-Type": "application/json"
        }
    });

    let url = window.location;
    let access_token = new URLSearchParams(url.search).get('token');

    console.log(access_token);

    api.put("http://localhost:8080/user/changePassword/", {
        token: access_token,
        oldPassword: oldPassword.value,
        newPassword: newPassword.value,
    }).then(res => {
        infoTable.style.display = 'block';
        infoTable.style.backgroundColor = 'rgba(0, 151, 0, 0.6)';
        infoTable.appendChild(createPForInfoTable("pPasswordChanged", "Password has been changed"));
    }).catch(error => {
        alert(error.response.data.message)
    });
}

function checkRepeatPassword() {
    if (newPassword.value === repeatPassword.value) {
        return true;
    } else {
        console.log("repeat")
        infoTable.appendChild(createPForInfoTable("pPasswordMatch", "Passwords doesn't match"));
        return false;
    }
}

function checkPasswordLenght() {
    if (newPassword.value.length >= 6 && newPassword.value.length <= 30) {
        return true;
    } else {
        console.log("lenght")
        infoTable.appendChild(createPForInfoTable("pPasswordLenght", "Password length between 6 and 30"));
        return false;
    }
}

function createPForInfoTable(idName, text) {
    let p = document.createElement("p")
    p.setAttribute("id", idName)
    let textNode = document.createTextNode(text);
    p.appendChild(textNode)

    return p;
}

function inputCorrectSymbols(input) {
    let usernameRegex = /^[a-zA-Z0-9]+$/;
    let validUsername = input.value.match(usernameRegex);
    if (validUsername != null === false) {
        infoTable.appendChild(createPForInfoTable("pPasswordSymbol", "Invalid Symbol"));
    }
    return validUsername != null;
}