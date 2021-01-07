let emailInput = document.getElementById('emailInput');

const apiTextPlain = axios.create({
    withCredentials: true,
    headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "text/plain"
    }
});

function sendEmail() {
    if (checkEmail()) {
        apiRequest()
    }
}

function apiRequest() {
    apiTextPlain.post("http://localhost:8080/user/changePasswordTokenRequest/",
    emailInput.value
        ).then(res => {
            alert(res.data);
        })
        .catch(error => {
        alert(error.response.data.message)
    });
}

function checkEmail() {
    let pattern = /^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/;
    let text = emailInput.value;

    if (text.match(pattern) == null) {
        alert("Invalid email")
        return false;
    } else {
        return true;
    }
}
