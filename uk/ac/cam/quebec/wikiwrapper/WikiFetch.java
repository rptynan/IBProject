package uk.ac.cam.quebec.wikiwrapper;

import java.util.List;

/**
 * Class providing methods to get Wikipedia articles. Is uninstantiable.
 * 
 * @author Stuart
 * 
 */
public class WikiFetch {

	/**
	 * Blocks construction.
	 */
	private WikiFetch() {
	}

	/**
	 * Search Wikipedia for articles simply by supplying a string. Can return an
	 * empty list.
	 * 
	 * @param searchTerm
	 *            String to search with.
	 * @param max
	 *            Maximum number of articles to return.
	 * @param edits
	 *            Number of edits to put with each article. Make zero to prevent
	 *            an extra query for edits to be made.
	 * @return List of fetched articles ordered in the order that WIkipedia
	 *         gives them. Can be empty.
	 * @throws WikiException Throws exception if connection fails.
	 */

	public static List<WikiArticle> search(String searchTerm, int max, int edits)
			throws WikiException {
		return null;
	}

}
