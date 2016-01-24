package uk.ac.cam.quebec.wikiwrapper;

import java.util.List;

/**
 * Class to represent a single Wikipedia article
 * 
 * @author Stuart
 * 
 */
public abstract class WikiArticle {

	private String title;
	private String extract;

	public String getTitle() {
		return title;
	}

	public String getExtract() {
		return extract;
	}

	private int views;
	private List<WikiEdit> edits;

	/**
	 * Constructor for an article. For performance reasons only the extract is
	 * gathered on initialisation.
	 * 
	 * @param title
	 *            The correct title for the article
	 * @throws WikiException
	 *             Throws exception if connection fails
	 */
	public WikiArticle(String title) throws WikiException {
		// TODO
	}

	/**
	 * Method to get usage data for a page. Is gathered on first call so there
	 * is likely to be a performance hit then. The data is monthly not daily so
	 * it may not be that useful and perhaps better to rely on search.
	 * 
	 * @throws WikiException
	 *             Throws exception if connection fails
	 * @return The number of views of the page in the past 30 days.
	 */
	public abstract int getViews() throws WikiException;

	/**
	 * Forms the URL of the page.
	 * 
	 * @return URL as a string
	 */
	public abstract String getURL();

	/**
	 * Method to get the edits list. If the editCount is less than or equal to
	 * the current length of the edit list then there is no call to Wikipeda and
	 * the cached list is returned. However if it is greater than then the
	 * entire edit list will be rebuilt (costly).
	 * 
	 * @param editCount
	 *            Number of edits wanted
	 * @return List of the edits
	 * @throws WikiException
	 *             Throws exception if connection fails
	 */

	public abstract List<WikiEdit> getEdits(int editCount) throws WikiException;

	/**
	 * Method that return the list of edits already gathered. Will never make a
	 * web query.
	 * 
	 * @return List of the edits.
	 */
	public abstract List<WikiEdit> getCachedEdits();

	/**
	 * Removes all cached edits
	 */
	public abstract void clearCachedEdits();

}
