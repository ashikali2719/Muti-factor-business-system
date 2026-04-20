import java.util.List;

public class DecisionResult {
    private int confidence;
    private String decisionLevel;
    private String decision;
    private String summary;
    private List<String> insights;
    private String explanation;

    public DecisionResult(int confidence, String decisionLevel, String decision, String summary, List<String> insights, String explanation) {
        this.confidence = confidence;
        this.decisionLevel = decisionLevel;
        this.decision = decision;
        this.summary = summary;
        this.insights = insights;
        this.explanation = explanation;
    }

    public int getConfidence() { return confidence; }
    public String getDecisionLevel() { return decisionLevel; }
    public String getDecision() { return decision; }
    public String getSummary() { return summary; }
    public List<String> getInsights() { return insights; }
    public String getExplanation() { return explanation; }
}