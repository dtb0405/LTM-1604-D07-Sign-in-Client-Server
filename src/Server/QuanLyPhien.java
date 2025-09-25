package Server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuanLyPhien {
    private static QuanLyPhien instance;
    private Map<String, ThongTinPhien> sessions;
    private Map<String, String> userSessions; // username -> sessionId

    private QuanLyPhien() {
        sessions = new ConcurrentHashMap<>();
        userSessions = new ConcurrentHashMap<>();
    }

    public static synchronized QuanLyPhien getInstance() {
        if (instance == null) {
            instance = new QuanLyPhien();
        }
        return instance;
    }

    public String createSession(String username, String clientAddress) {
        // Xóa session cũ nếu có
        if (userSessions.containsKey(username)) {
            String oldSessionId = userSessions.get(username);
            sessions.remove(oldSessionId);
        }

        // Tạo session mới
        String sessionId = generateSessionId();
        ThongTinPhien session = new ThongTinPhien(sessionId, username, clientAddress);
        
        sessions.put(sessionId, session);
        userSessions.put(username, sessionId);
        
        System.out.println("✅ Tạo session mới: " + username + " (" + sessionId + ")");
        return sessionId;
    }

    public boolean validateSession(String sessionId) {
        ThongTinPhien session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        // Kiểm tra session có hết hạn không (30 phút)
        long currentTime = System.currentTimeMillis();
        if (currentTime - session.getLastActivity() > 30 * 60 * 1000) {
            removeSession(sessionId);
            return false;
        }
        
        // Cập nhật thời gian hoạt động cuối
        session.updateLastActivity();
        return true;
    }

    public void removeSession(String sessionId) {
        ThongTinPhien session = sessions.remove(sessionId);
        if (session != null) {
            userSessions.remove(session.getUsername());
            System.out.println("❌ Xóa session: " + session.getUsername() + " (" + sessionId + ")");
        }
    }

    public void removeSessionByUsername(String username) {
        String sessionId = userSessions.remove(username);
        if (sessionId != null) {
            sessions.remove(sessionId);
            System.out.println("❌ Xóa session theo username: " + username);
        }
    }

    public ThongTinPhien getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public ThongTinPhien getSessionByUsername(String username) {
        String sessionId = userSessions.get(username);
        return sessionId != null ? sessions.get(sessionId) : null;
    }

    public List<ThongTinPhien> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    public List<String> getOnlineUsers() {
        List<String> onlineUsers = new ArrayList<>();
        for (ThongTinPhien session : sessions.values()) {
            if (validateSession(session.getSessionId())) {
                onlineUsers.add(session.getUsername());
            }
        }
        return onlineUsers;
    }

    public int getOnlineUserCount() {
        return getOnlineUsers().size();
    }

    public void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredSessions = new ArrayList<>();
        
        for (Map.Entry<String, ThongTinPhien> entry : sessions.entrySet()) {
            ThongTinPhien session = entry.getValue();
            if (currentTime - session.getLastActivity() > 30 * 60 * 1000) {
                expiredSessions.add(entry.getKey());
            }
        }
        
        for (String sessionId : expiredSessions) {
            removeSession(sessionId);
        }
        
        if (!expiredSessions.isEmpty()) {
            System.out.println("🧹 Đã dọn dẹp " + expiredSessions.size() + " session hết hạn");
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static class ThongTinPhien {
        private String sessionId;
        private String username;
        private String clientAddress;
        private long createTime;
        private long lastActivity;

        public ThongTinPhien(String sessionId, String username, String clientAddress) {
            this.sessionId = sessionId;
            this.username = username;
            this.clientAddress = clientAddress;
            this.createTime = System.currentTimeMillis();
            this.lastActivity = this.createTime;
        }

        public void updateLastActivity() {
            this.lastActivity = System.currentTimeMillis();
        }

        // Getters
        public String getSessionId() { return sessionId; }
        public String getUsername() { return username; }
        public String getClientAddress() { return clientAddress; }
        public long getCreateTime() { return createTime; }
        public long getLastActivity() { return lastActivity; }

        public String getSessionDuration() {
            long duration = System.currentTimeMillis() - createTime;
            long minutes = duration / (60 * 1000);
            long seconds = (duration % (60 * 1000)) / 1000;
            return String.format("%d phút %d giây", minutes, seconds);
        }

        @Override
        public String toString() {
            return String.format("Session[%s] User: %s, Address: %s, Duration: %s", 
                sessionId.substring(0, 8), username, clientAddress, getSessionDuration());
        }
    }
}

