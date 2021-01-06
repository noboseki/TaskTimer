let timerValue = document.getElementById('timer');

timer();

function timer() {
    'use strict';

    let output = document.getElementById('timer');
    let toggle = document.getElementById('toggle');
    let clear = document.getElementById('clear');
    let running = false;
    let paused = false;
    let timer;

    let then;
    let delay;
    let delayThen;

    let start = function () {
    if (document.getElementById('select').options.length == 0) {
        alert("Please select ant task")
    } else {
        delay = 0;
        running = true;
        then = Date.now();
        timer = setInterval(run, 51);
        toggle.innerHTML = '<i class="fas fa-pause"></i>';
        taskSelectContainer.disabled = true;
    }
    };

    let parseTime = function (elapsed) {
        let d = [3600000, 60000, 1000];
        let time = [];
        let i = 0;

        while (i < d.length) {
            let t = Math.floor(elapsed / d[i]);

            elapsed -= t * d[i];

            t = (i > 0 && t < 10) ? '0' + t : t;
            time.push(t);
            i++;
        }

        return time;
    };

    let run = function () {
        let time = parseTime(Date.now() - then - delay);
        output.innerHTML = time[0] + ':' + time[1] + ':' + time[2];
    };

    let stop = function () {
        paused = true;
        delayThen = Date.now();
        toggle.innerHTML = '<i class="fas fa-play"></i>';
        clear.style.display = 'inline-block'
        clearInterval(timer);
        run();
    };

    let resume = function () {
        paused = false;
        delay += Date.now() - delayThen;
        timer = setInterval(run, 51);
        toggle.innerHTML = '<i class="fas fa-pause"></i>';
        clear.style.display = 'none'
    };

    let reset = function () {
        let tmp = confirm("Are you sure to stop session?");
        if (tmp === true) {
            running = false;
            paused = false;
            checkAndCreateSession();
            taskSelectContainer.disabled = false;
            toggle.innerHTML = '<i class="fas fa-play">';
            output.innerHTML = '0:00:00';
            clear.style.display = 'none'
        }
    };

    let router = function () {
        if (!running) start();
        else if (paused) resume();
        else stop();
    };

    toggle.addEventListener('click', router);
    clear.addEventListener('click', reset);

}

function checkAndCreateSession() {
    if (taskSelectContainer.value === "" || timerValue.textContent === '0:00:00') {
        alert("You mast pick any task or have time longer than 0s")
    } else {
        createSession();
    }
}

function createSession() {
    const api = axios.create({
        withCredentials: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
            "Content-Type": "application/json"
        }
    });

    api.post("http://localhost:8080/session/create/", {
        date: getTodayDate(),
        time: timerValue.textContent,
        taskName: taskSelectContainer.value,
    }).then(res => getTasks())
        .catch(error => {
            alert(error.response.data.message)
        });
}

function getTodayDate() {
    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0');
    let yyyy = today.getFullYear();

    return yyyy + '-' + mm + '-' + dd;
}