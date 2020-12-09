let taskSelectContainer = document.getElementById('tasksList')
let tBodyRef = document.getElementById('tBody');
const apiPut = axios.create({
    withCredentials: true,
    headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "text/plain"
    }
});

getTasks();

function getTasks() {
    const api = axios.create({
        withCredentials: true
    });

    api.get('http://localhost:8080/task/getTasks/').then(res => {
        tBodyRef.innerHTML = "";
        taskSelectContainer.innerHTML = "";
        res.data.forEach(f => {
            if (f.complete === false) {
                let opt = document.createElement('option');
                opt.innerHTML = f.taskName;
                opt.value = f.taskName;
                taskSelectContainer.appendChild(opt);
            }
            addRow(f.taskName, f.time, f.sessionsNumber, f.complete)
        })
    })
}

function addRow(taskName, time, sessions, complete) {
    let newRow = tBodyRef.insertRow(-1);

    let taskNameCell = newRow.insertCell(0);
    let timeCell = newRow.insertCell(1);
    let sessionCell = newRow.insertCell(2);
    let completeCell = newRow.insertCell(3);

    let taskNameText = document.createTextNode(taskName);
    let timeText = document.createTextNode(time);
    let sessionText = document.createTextNode(sessions);

    taskNameCell.appendChild(taskNameText);
    timeCell.appendChild(timeText);
    sessionCell.appendChild(sessionText);
    completeCell.appendChild(createCompleteButton(complete, taskName))
    // buttonCell.appendChild(createArchiveButton(taskName))
}

function createArchiveButton(taskName) {
    let buttonArchive = document.createElement("button");
    buttonArchive.classList.add("archive")
    buttonArchive.innerHTML = "<i class=\"fas fa-archive\"></i>"
    buttonArchive.setAttribute("onclick", "archiveAlert(" + "\"" + taskName + "\"" + ")")
    return buttonArchive;
}

function changeTaskComplete(taskName) {
    apiPut.put("http://localhost:8080/task/changeTaskComplete/",
        taskName).then(res => getTasks());
}

function changeTaskArchive(taskName) {
    apiPut.put("http://localhost:8080/task/changeTaskArchive/",
        taskName).then(res => getTasks());
}

function archiveAlert(taskName) {
    let tmp = confirm("Are you sure to archive task?");
    if (tmp === true) {
        changeTaskArchive(taskName);
    } else {
    }
}

function createCompleteButton(isComplete, taskName) {
    let buttonComplete = document.createElement("button");
    buttonComplete.setAttribute("onclick",
        "changeTaskComplete(" + "\"" + taskName + "\"" + ")")

    if (isComplete === true) {
        buttonComplete.classList.add("complete")
        buttonComplete.innerHTML = "<i class=\"fas fa-check-circle\"></i>"
    } else {
        buttonComplete.classList.add("incomplete")
        buttonComplete.innerHTML = "<i class=\"fas fa-times-circle\"></i>"
    }

    return buttonComplete;
}