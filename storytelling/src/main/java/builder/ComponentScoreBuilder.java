package builder;

public interface ComponentScoreBuilder {
    public double computeScore(Boolean[][] modelComponent, Double[][] surpriseScore);
}
