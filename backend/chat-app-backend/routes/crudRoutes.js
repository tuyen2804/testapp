const express = require('express');
const router = express.Router();
const mysql = require('mysql2');
const config = require('../config/config');

const connection = mysql.createConnection(config.db);

// Middleware xác thực JWT
const verifyToken = require('../middlewares/authMiddleware');

// 1. Thêm (Add)
router.post('/add', verifyToken, (req, res) => {
    const { tableName, columns, values } = req.body;

    if (columns.length !== values.length) {
        return res.status(400).send('Number of columns and values do not match.');
    }

    const placeholders = values.map(() => '?').join(', ');
    const query = `INSERT INTO ${mysql.escapeId(tableName)} (${columns.map(col => mysql.escapeId(col)).join(', ')}) VALUES (${placeholders})`;

    connection.query(query, values, (err, results) => {
        if (err) return res.status(500).send('Error on the server.');
        res.status(201).send('Record added successfully.');
    });
});

// 2. Tìm kiếm (Search)
router.post('/search', verifyToken, (req, res) => {
    const { tableName, columnName, columnValue } = req.body;

    const query = `SELECT * FROM ${mysql.escapeId(tableName)} WHERE ${mysql.escapeId(columnName)} = ?`;

    connection.query(query, [columnValue], (err, results) => {
        if (err) return res.status(500).send('Error on the server.');
        res.status(200).json(results);
    });
});

// 3. Xóa (Delete)
router.post('/delete', verifyToken, (req, res) => {
    const { tableName, columnName, columnValue } = req.body;

    const query = `DELETE FROM ${mysql.escapeId(tableName)} WHERE ${mysql.escapeId(columnName)} = ?`;

    connection.query(query, [columnValue], (err, results) => {
        if (err) return res.status(500).send('Error on the server.');
        res.status(200).send('Record deleted successfully.');
    });
});

// 4. Cập nhật (Update)
router.post('/update', verifyToken, (req, res) => {
    const { tableName, columnName, columnValue, updateColumns, updateValues } = req.body;

    if (updateColumns.length !== updateValues.length) {
        return res.status(400).send('Number of columns and values do not match.');
    }

    const setClause = updateColumns.map(col => `${mysql.escapeId(col)} = ?`).join(', ');

    const query = `UPDATE ${mysql.escapeId(tableName)} SET ${setClause} WHERE ${mysql.escapeId(columnName)} = ?`;

    const queryParams = [...updateValues, columnValue];

    connection.query(query, queryParams, (err, results) => {
        if (err) return res.status(500).send('Error on the server.');
        res.status(200).send('Record updated successfully.');
    });
});

module.exports = router;
