let timerValue = document.getElementById('timer');

timer();

function timer() {
	'use strict';

	//declare
	var output = document.getElementById('timer');
	var toggle = document.getElementById('toggle');
	var clear = document.getElementById('clear');
	var running = false;
	var paused = false;
	var timer;
	
	// timer start time
	var then;
	// pause duration
	var delay;
	// pause start time
	var delayThen;
	
	// start timer
	var start = function() {
		delay = 0;
		running = true;
		then = Date.now();
		timer = setInterval(run,51);
        toggle.innerHTML = '<i class="fas fa-pause"></i>';
        taskSelectContainer.disabled = true;

	};
	
	// parse time in ms for output
	var parseTime = function(elapsed) {
		// array of time multiples [hours, min, sec, decimal]
		var d = [3600000,60000,1000];
		var time = [];
		var i = 0;

		while (i < d.length) {
			var t = Math.floor(elapsed/d[i]);

			// remove parsed time for next iteration
			elapsed -= t*d[i];

			// add '0' prefix to m,s,d when needed
			t = (i > 0 && t < 10) ? '0' + t : t;
			time.push(t);
			i++;
		}
		
		return time;
	};
	
	// run
	var run = function() {
		// get output array and print
		var time = parseTime(Date.now()-then-delay);
		output.innerHTML = time[0] + ':' + time[1] + ':' + time[2];
	};
	
	// stop
	var stop = function() {
		paused = true;
		delayThen = Date.now();
        toggle.innerHTML = '<i class="fas fa-play"></i>';
        clear.style.display = 'inline-block'
		clearInterval(timer);
		// call one last time to print exact time
		run();
	};
	
	// resume
	var resume = function() {
		paused = false;
		delay += Date.now()-delayThen;
		timer = setInterval(run,51);
		toggle.innerHTML = '<i class="fas fa-pause"></i>';
		clear.style.display = 'none'
	};
	
	// clear
	var reset = function() {
		running = false;
		paused = false;
		updateProfile();
		taskSelectContainer.disabled = false;
		toggle.innerHTML = '<i class="fas fa-play">';
		output.innerHTML = '0:00:00';
		clear.style.display = 'none'
	};
	
	// evaluate and route
	var router = function() {
		if (!running) start();
		else if (paused) resume();
		else stop();
	};
	
	toggle.addEventListener('click',router);
	clear.addEventListener('click',reset);
	
}

function getTodayDate(){
	let today = new Date();
	let dd = String(today.getDate()).padStart(2, '0');
	let mm = String(today.getMonth() + 1).padStart(2, '0');
	let yyyy = today.getFullYear();

	return  yyyy + '-' + mm + '-' + dd;
}

function updateProfile() {
	const api = axios.create({
		withCredentials: true,
		headers: {
			"Access-Control-Allow-Origin": "*",
			"Content-Type": "application/json"
		}
	});
	console.log("update")
	console.log(getTodayDate());
	console.log(timerValue.textContent)
	console.log(taskSelectContainer.value)
	api.post("http://localhost:8080/session/create/", {
		date: getTodayDate(),
		time:  timerValue.textContent,
		taskName: taskSelectContainer.value,
	});
}
