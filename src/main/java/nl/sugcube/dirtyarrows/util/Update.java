package nl.sugcube.dirtyarrows.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Gravity's Simple Update Checker
 * This class is a barebones example of how to use the BukkitDev ServerMods API to check for file updates.
 */
public class Update {

    // The project's unique ID
    private final int projectID;

    // An optional API key to use, will be null if not submitted
    private String apiKey = "";

    // The current version of the plugin.
    private final String currentVersion;

    // Keys for extracting file information from JSON response
    private static final String API_NAME_VALUE = "name";

    // Static information for querying the API
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";

    /**
     * Check for updates anonymously (keyless)
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     * @param version (String) The current version the server is running.
     */
    public Update(int projectID, String version) {
        this.projectID = projectID;
        this.currentVersion = version.replaceAll("([a-z]|[A-Z])", "");
    }

    /**
     * Check for updates using your Curse account (with key)
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     * @param version (String) the version the server is running.
     * @param apiKey Your ServerMods API key, found at https://dev.bukkit.org/home/servermods-apikey/
     */
    public Update(int projectID, String version, String apiKey) {
        this.projectID = projectID;
        this.currentVersion = version.replaceAll("([a-z]|[A-Z])", "");;
        this.apiKey = apiKey;
        query();
    }

    /**
     * Query the API to find the latest approved file's details.
     * @return (boolean) True if there is an update avaiable, False if the plugin is up-to-date or if an exception has been thrown.
     */
    public boolean query() {
        URL url = null;

        try {
            // Create the URL to query using the project's ID
            url = new URL(API_HOST + API_QUERY + projectID);
        } catch (MalformedURLException e) {
            // There was an error creating the URL
            e.printStackTrace();
            return false;
        }

        try {
            // Open a connection and query the project
            URLConnection conn = url.openConnection();

            if (apiKey != null) {
                // Add the API key to the request if present
                conn.addRequestProperty("X-API-Key", apiKey);
            }

            // Add the user-agent to identify the program
            conn.addRequestProperty("User-Agent", "ServerModsAPI-Example (by Gravity)");

            // Read the response of the query
            // The response will be in a JSON format, so only reading one line is necessary.
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();

            // Parse the array of files from the query's response
            JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() > 0) {
                // Get the newest file's details
                JSONObject latest = (JSONObject) array.get(array.size() - 1);

                // Get the version's title
                String versionName = (String) latest.get(API_NAME_VALUE);

		// Compares the file name with the current version.
		String latestVersion = versionName.replace("DirtyArrows [", "")
						  .replace("[a-z]", "")
						  .replace("[A-Z]", "")
						  .replace("]", "")
						  .replace("a", "")
						  .replace("b", "")
						  .replace("v", "");

		if (latestVersion.equalsIgnoreCase(currentVersion)) {
		    return false;
		} else {
		    return true;
		}
            } else {
                System.out.println("[DirtyArrows] There are no files for this project");
		return false;
            }
        } catch (IOException e) {
            // There was an error reading the query
            e.printStackTrace();
            return false;
        }
    }
}