package entityClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyList {
    
    private List<Reply> replies;           
    
   
    public ReplyList() {
        this.replies = new ArrayList<>();
    }
    
  
    public ReplyList(List<Reply> replies) {
        this.replies = new ArrayList<>(replies);
    }
    
  
    public void addReply(Reply reply) {
        if (reply != null) {
            replies.add(reply);
        }
    }
    
   
    public boolean removeReply(Reply reply) {
        return replies.remove(reply);
    }
    
  
    public boolean removeReplyById(int replyId) {
        return replies.removeIf(r -> r.getReplyId() == replyId);
    }
    
    
    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies);
    }
    
   
    public Reply getReplyById(int replyId) {
        for (Reply reply : replies) {
            if (reply.getReplyId() == replyId) {
                return reply;
            }
        }
        return null;
    }
    

    public int size() {
        return replies.size();
    }
    

    public boolean isEmpty() {
        return replies.isEmpty();
    }
    
    public void clear() {
        replies.clear();
    }
    
    public ReplyList filterByPostId(int postId) {
        List<Reply> filtered = replies.stream()
                .filter(r -> r.getPostId() == postId)
                .collect(Collectors.toList());
        return new ReplyList(filtered);
    }
    
    public ReplyList filterByAuthor(String author) {
        List<Reply> filtered = replies.stream()
                .filter(r -> r.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
        return new ReplyList(filtered);
    }
    
    public ReplyList searchByKeywords(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ReplyList(replies); // Return all if no search text
        }
        
        List<Reply> filtered = replies.stream()
                .filter(r -> r.matchesAllKeywords(searchText))
                .collect(Collectors.toList());
        return new ReplyList(filtered);
    }
    
    public int getReplyCountForPost(int postId) {
        return (int) replies.stream()
                .filter(r -> r.getPostId() == postId)
                .count();
    }
    
    public ReplyList sortByNewest() {
        List<Reply> sorted = new ArrayList<>(replies);
        sorted.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));
        return new ReplyList(sorted);
    }
    
    public ReplyList sortByOldest() {
        List<Reply> sorted = new ArrayList<>(replies);
        sorted.sort((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()));
        return new ReplyList(sorted);
    }
    
    @Override
    public String toString() {
        return "ReplyList{" +
                "count=" + replies.size() +
                " replies}";
    }
}