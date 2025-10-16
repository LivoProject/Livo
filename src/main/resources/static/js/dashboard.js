document.addEventListener("DOMContentLoaded", function() {
    // 차트1
    const ctx1 = document.getElementById("chart1").getContext("2d");
    new Chart(ctx1, {
        type: "bar",
        data: {
            labels: ["A", "B", "C", "D"],
            datasets: [{
                label: "통계1",
                data: [10, 20, 30, 40],
                backgroundColor: "rgba(54, 162, 235, 0.5)"
            }]
        }
    });

    // 차트2
    const ctx2 = document.getElementById("chart2").getContext("2d");
    new Chart(ctx2, {
        type: "line",
        data: {
            labels: ["A", "B", "C", "D"],
            datasets: [{
                label: "통계2",
                data: [15, 25, 35, 45],
                borderColor: "rgba(255, 99, 132, 1)",
                fill: false
            }]
        }
    });

    // 차트3
    const ctx3 = document.getElementById("chart3").getContext("2d");
    new Chart(ctx3, {
        type: "pie",
        data: {
            labels: ["A", "B", "C"],
            datasets: [{
                label: "통계3",
                data: [30, 50, 20],
                backgroundColor: [
                    "rgba(255, 99, 132, 0.5)",
                    "rgba(54, 162, 235, 0.5)",
                    "rgba(255, 206, 86, 0.5)"
                ]
            }]
        }
    });

});
