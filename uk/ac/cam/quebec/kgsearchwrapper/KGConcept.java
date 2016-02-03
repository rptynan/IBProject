package uk.ac.cam.quebec.kgsearchwrapper;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Class representing a concept identified by the Google Knowledge Graph. For now it has only a few
 * most relevant fields, such as name, short description, detailed description and relevant URLs.
 * Any of these might be empty but not null.
 *
 * Example concept for the query "Donald Trump"
 *
 * name: "Donald Trump"
 * description: "Business magnate"
 * detailed description: "Donald John Trump is an American business magnate, billionaire, investor,
 * socialite, author, television personality, and candidate for President of the United States
 * in the 2016 presidential election. "
 * score: "1002.246948"
 * types {
 *   "Person"
 *   "Thing"
 * }
 * relevant urls {
 *   "http://en.wikipedia.org/wiki/Donald_Trump"
 * }
 *
 * @author tudor
 */
public class KGConcept {

    private String name;
    private List<String> types;
    private String description;
    private String detailed_description;
    private List<String> relevant_urls;
    private double score = 0.0;

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public void addType(String type) {
        if (type == null) return;
        if (types == null) types = new LinkedList<>();
        types.add(type);
    }

    @Nonnull
    public List<String> getTypes() {
        if (types == null) types = new LinkedList<>();
        return types;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nonnull
    public String getDescription() {
        if (description != null) {
            return description;
        }
        return "";
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailed_description = detailedDescription;
    }

    @Nonnull
    public String getDetailedDescription() {
        if (detailed_description != null) {
            return detailed_description;
        }
        return "";
    }

    public void addRelevantURL(String url) {
        if (url == null) return;
        if (relevant_urls == null) relevant_urls = new LinkedList<>();
        relevant_urls.add(url);
    }

    @Nonnull
    public List<String> getRelevantURLs() {
        if (relevant_urls == null) relevant_urls = new LinkedList<>();
        return relevant_urls;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setScore(String score) {
        try {
            setScore(Double.parseDouble(score));
        } catch (NumberFormatException e) {
            setScore(0.0);
        }
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("name: ");
        builder.append("\"").append(getName()).append("\"");
        builder.append('\n');

        builder.append("description: ");
        builder.append("\"").append(getDescription()).append("\"");
        builder.append('\n');

        builder.append("detailed description: ");
        builder.append("\"").append(getDetailedDescription()).append("\"");
        builder.append('\n');

        builder.append("score: ");
        builder.append("\"").append(getScore()).append("\"");
        builder.append('\n');

        builder.append("types {\n");
        for (String type : getTypes()) {
            builder.append("\"").append(type).append("\"");
            builder.append('\n');
        }
        builder.append("}\n");

        builder.append("relevant urls {\n");
        for (String url : getRelevantURLs()) {
            builder.append("\"").append(url).append("\"");
            builder.append('\n');
        }
        builder.append("}\n");

        return builder.toString();
    }
}
