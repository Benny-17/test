const express = require('express');
const app = express();

const PORT = process.env.PORT || 3000;

app.get('/', (req, res) => {
  res.send(`
    <h2>EKS</h2>
    <p><strong>Environment:</strong> ${process.env.NODE_ENV}</p>
    <p><strong>App:</strong> ${process.env.APP_NAME}</p>
    <p><strong>Region:</strong> ${process.env.REGION}</p>
    <p><strong>API_KEY:</strong> ${process.env.API_KEY}</p>
  `);
});

app.listen(PORT, () => {
  console.log(`App running on port ${PORT}`);
});

