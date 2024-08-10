module.exports = {
    db: {
        host: 'localhost',
        user: 'root',
        password: '1234',
        database: 'testbe',
        waitForConnections: true, // Cho phép hàng đợi khi kết nối bị thiếu
        connectionLimit: 10, // Số kết nối tối đa trong pool
        queueLimit: 0 // Số lượng kết nối tối đa được xếp hàng (0 là không giới hạn)
    },
    jwtSecret: 'ahihi123' // Khóa bí mật cho JWT
};

