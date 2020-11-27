let bar_ctx = document.getElementById('bar-chart').getContext('2d');
let inputFromDate = document.getElementById('fromData');
let inputToDate = document.getElementById('toData');
let tableBody = document.getElementById('tBody')

let date = new Date();
inputToDate.value = date.toISOString().substr(0,10);

date = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
inputFromDate.value = date.toISOString().substr(0, 10);

getTableData();
getChainDataAndBuild();

function getTableData() {
    const api = axios.create({
        withCredentials: true
    });

    api.get('http://localhost:8080/session/getTableByDate/' + inputFromDate.value + "/" + inputToDate.value).then(res => {
        tableBody.innerHTML = "";
        res.data.forEach( f => {
            addRow('tBody', f.date, f.time, f.sessions)
        })
    })
}

    function getChainDataAndBuild() {
        const api = axios.create({
            withCredentials: true
        });

        api.get('http://localhost:8080/session/getChainByDate/' + inputFromDate.value + "/" + inputToDate.value).then(res => {
            buildChart(res.data.dateLabel, res.data.dataList)
            })
    }

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
        window.bar_chart = new Chart(bar_ctx, {
            type: 'bar',
            data: {
                labels: label,
                datasets: [
                ],
            },
            options: {
                legend: {
                    display: false
                },
                scales: {
                    xAxes: [{
                        stacked: true,
                        gridLines: {
                            display:false
                        }
                    }],
                    yAxes: [{
                        stacked: true,
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
        insertDataSets(data);
        window.bar_chart.update();
    }

    function insertDataSets(data){
        let i=0;
        let colours = ['#6c58af','#58af6c','#af589b']
        data.forEach(element => {
            bar_chart.data.datasets.push({
                data:   element.data,
                label: element.taskName,
                backgroundColor: colours[i],
            });

            if (i == 3) {
                i = 0;
            } else {
                i++;
            }
        });
    }


