let createInpEmail = document.getElementById('createInpEmail');
let createInpUsername = document.getElementById('createInpUsername');
let createInpPassword = document.getElementById('createInpPassword');
let createInpRepeatPassword = document.getElementById('createInpRepeatPassword');
let infoTable = document.getElementById('infoTable');

infoTable.appendChild(createPForInfoTable("pUsernameLength", "Username length between 6 and 14"));
infoTable.appendChild(createPForInfoTable("pUsernameValid", "Valid username symbol"));
infoTable.appendChild(createPForInfoTable("pEmail", "Valid email"));
infoTable.appendChild(createPForInfoTable("pPasswordValid", "Valid password symbol"));
infoTable.appendChild(createPForInfoTable("pPasswordLength", "Password length between 10 and 30"));
infoTable.appendChild(createPForInfoTable("pUserCreated", "Active emile has been send"));

document.getElementById('navLogout').style.display = 'none';

function createUser() {
    document.getElementById('pUserCreated').style.display = 'none';
    infoTable.style.display = 'none';
    if (checkFields()) {
        const createApi = axios.create({
            withCredentials: true,
            headers: {
                "Access-Control-Allow-Origin": "*",
                "Content-Type": "application/json"
            }
        });

        createApi.post("http://localhost:8080/user/create", {
            email: createInpEmail.value,
            password: createInpPassword.value,
            userName: createInpRepeatPassword.value,
        }).then(res => {
            cleanForm();
            infoTable.style.backgroundColor = "rgba(16, 194, 16, 0.7)"
            infoTable.style.display = 'block';
            document.getElementById('pUserCreated').style.display = 'block';
        }).catch(error => {
            alert(error.response.data.message)
        });
    }
}

function checkFields() {
    let usernameCorrect = false;
    let passwordCorrect = false;
    let repeatPasswordCorrect = false;
    let emailCorrect = checkEmail();

    if (checkLength(5, 15, createInpUsername, "pUsernameLength")) {
        if (checkValid(createInpUsername, "pUsernameValid")) {
            createInpUsername.style.backgroundColor = 'rgba(74, 209, 74, 0.7)';
            usernameCorrect = true;
        }
    }

    if (checkLength(7, 31, createInpPassword, "pPasswordLength")) {
        if (checkValid(createInpPassword, "pPasswordValid")) {
            createInpPassword.style.backgroundColor = 'rgba(74, 209, 74, 0.7)';
            passwordCorrect = true;
        }
    }

    if (createInpPassword.value === createInpRepeatPassword.value && createInpPassword.value.length > 0) {
        createInpRepeatPassword.style.backgroundColor = 'rgba(74, 209, 74, 0.7)';
        repeatPasswordCorrect = true;
    } else {
        createInpRepeatPassword.value = "";
        createInpRepeatPassword.style.backgroundColor = "rgba(255, 50, 50, 0.7)"
    }

    return usernameCorrect && passwordCorrect && repeatPasswordCorrect && emailCorrect;
}

function cleanForm() {
    createInpEmail.value = "";
    createInpUsername.value = "";
    createInpPassword.value = "";
    createInpRepeatPassword.value = "";

    createInpEmail.style.backgroundColor = 'white';
    createInpUsername.style.backgroundColor = 'white';
    createInpPassword.style.backgroundColor = 'white';
    createInpRepeatPassword.style.backgroundColor = 'white';
}

function checkEmail() {
    let pattern = /^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/;
    let text = createInpEmail.value;

    if (text.match(pattern) == null) {
        alert(createInpEmail, "pEmail")
        return false;
    } else {
        let p = document.getElementById('pEmail');
        p.style.display = 'none'
        createInpEmail.style.backgroundColor = 'rgba(74, 209, 74, 0.7)';
        return true;
    }
}

function checkLength(from, to, input, pIdForInput) {
    if (from < input.value.length && input.value.length < to) {
        let p = document.getElementById(pIdForInput);
        p.style.display = 'none'
        return true;
    } else {
        alert(input, pIdForInput)
        return false;
    }
}

function checkValid(input, pIdForInput) {
    if (usernameIsValid(input, input.value)) {
        let p = document.getElementById(pIdForInput);
        p.style.display = 'none'
        return true;
    } else {
        alert(input, pIdForInput)
        return false;
    }
}

function alert(input, pId) {
    infoTable.style.backgroundColor = "rgba(255, 0, 0, 0.6)";
    infoTable.style.display = 'block';
    input.style.backgroundColor = "rgba(255, 50, 50, 0.7)"
    document.getElementById(pId).style.display = 'block';
}

function usernameIsValid(input) {
    let usernameRegex = /^[a-zA-Z0-9]+$/;
    let validUsername = input.value.match(usernameRegex);
    return validUsername != null;


}

function createPForInfoTable(idName, text) {
    let p = document.createElement("p")
    p.setAttribute("id", idName)
    let textNode = document.createTextNode(text);
    p.appendChild(textNode)

    return p;
}