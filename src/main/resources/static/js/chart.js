let bar_ctx = document.getElementById('bar-chart').getContext('2d');
let inputFromDate = document.getElementById('fromData');
let inputToDate = document.getElementById('toData');

let date = new Date();
inputToDate.value = date.toISOString().substr(0,10);

date = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
inputFromDate.value = date.toISOString().substr(0, 10);

    function axiosTest() {
    const api = axios.create({
        withCredentials: true
    });

    api.get('http://localhost:8080/session/getBetweenDate/' + inputFromDate.value + "/" + inputToDate.value).then(res => {
        let date = [];
        let time = [];

        res.data.forEach( f => {
            date.push(f.date);
            time.push(f.timeByNumber)
            addRow('tBody', f.date, f.time, f.sessions)
        })
        buildChart(date, time);
    })
}

axiosTest();

function addRow(tableID, date, time, session ) {
    let tBodyRef = document.getElementById(tableID);

    let newRow = tBodyRef.insertRow(-1);

    let dateCell = newRow.insertCell(0);
    let timeCell = newRow.insertCell(1);
    let sessionCell = newRow.insertCell(2);

    let dateText = document.createTextNode(date);
    let timeText = document.createTextNode(time);
    let sessionText = document.createTextNode(session);

    dateCell.appendChild(dateText);
    timeCell.appendChild(timeText);
    sessionCell.appendChild(sessionText);
}


function buildChart(label, data){
    new Chart(bar_ctx, {
        type: 'bar',
        data: {
            labels: label,
            datasets: [{
                data: data,
                            backgroundColor: 'rgb(108,88,175)',
                            hoverBorderColor: 'purple'
            }]
        },
        options: {
            legend: {
                display: false
            },
            scales: {
                xAxes: [{
                    gridLines: {
                        display:false
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero:true
                    },
                    gridLines: {
                        display:false
                    }
                }]
            }
        }
    });
}



