const express = require('express');
const bodyParser = require('body-parser');
const crudRoutes = require('./routes/crudRoutes');
const mysql = require('mysql2');
const config = require('./config/config');

const app = express();
const port = 3000;

// Thiết lập kết nối MySQL sử dụng createPool
const pool = mysql.createPool(config.db);

// Kiểm tra kết nối với MySQL
pool.getConnection((err, connection) => {
    if (err) {
        console.error('Error connecting to MySQL:', err.stack);
        return;
    }
    console.log('Connected to MySQL as ID ' + connection.threadId);
    connection.release(); // Giải phóng kết nối sau khi kiểm tra xong
});

app.use(bodyParser.json());
app.use('/api', crudRoutes);

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});

module.exports = pool;
