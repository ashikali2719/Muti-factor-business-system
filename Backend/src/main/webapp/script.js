document.getElementById('decisionForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const formData = new FormData(this);
    fetch('/decision', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('productName').textContent = data.productName;
        document.getElementById('competitorPrice').textContent = data.competitorPrice;
        document.getElementById('confidence').textContent = data.confidence;
        document.getElementById('decisionLevel').textContent = data.decisionLevel;
        document.getElementById('decision').textContent = data.decision;
        document.getElementById('summary').textContent = data.summary;
        document.getElementById('results').style.display = 'block';
    })
    .catch(error => console.error('Error:', error));
});