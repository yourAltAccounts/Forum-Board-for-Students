package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Reply {
    
  
    private int replyId;                  
    private int postId;                  
    private String author;                
    private String content;                 
    private LocalDateTime timestamp;        
    
   
    public Reply() {
        this.replyId = -1;
        this.postId = -1;
        this.author = "";
        this.content = "";
        this.timestamp = LocalDateTime.now();
    }
    
   
    public Reply(int postId, String author, String content) {
        this.replyId = -1;  // Will be set by database
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
  
    public Reply(int replyId, int postId, String author, String content, 
                 LocalDateTime timestamp) {
        this.replyId = replyId;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }
    

    
    public int getReplyId() {
        return replyId;
    }
    
    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
  
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        String lowerContent = content.toLowerCase();
        
        return lowerContent.contains(lowerKeyword);
    }
    
    public boolean matchesAllKeywords(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }
        
        String[] keywords = searchText.trim().split("\\s+");
        
        for (String keyword : keywords) {
            if (!matchesKeyword(keyword)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return "Reply{" +
                "replyId=" + replyId +
                ", postId=" + postId +
                ", author='" + author + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
}