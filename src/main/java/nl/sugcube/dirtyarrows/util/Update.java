package nl.sugcube.dirtyarrows.util;

import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Gravity's Simple Update Checker This class is a barebones example of how to use the BukkitDev
 * ServerMods API to check for file updates.
 *
 * Older version of: https://github.com/gravitylow/Updater
 *
 * @author gravitylow
 */
public class Update {

    // The project's unique ID
    private final int projectID;

    // The current version of the plugin.
    private final String currentVersion;

    // The new version of the plugin that is available.
    private String latestVersion;

    // Keys for extracting file information from JSON response
    private static final String API_NAME_VALUE = "name";

    // Static information for querying the API
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";

    /**
     * Check for updates anonymously (keyless)
     *
     * @param projectID
     *         The BukkitDev Project ID, found in the "Facts" panel on the right-side of your
     *         project page.
     * @param version
     *         (String) The current version the server is running.
     */
    public Update(int projectID, String version) {
        this.projectID = projectID;
        this.currentVersion = version.replaceAll("([a-z]|[A-Z])", "");
    }

    /**
     * Query the API to find the latest approved file's details.
     *
     * @return (boolean) True if there is an update avaiable, False if the plugin is up-to-date or
     * if an exception has been thrown.
     */
    public boolean query() {
        URL url;

        try {
            // Create the URL to query using the project's ID
            url = new URL(API_HOST + API_QUERY + projectID);
        }
        catch (MalformedURLException e) {
            // There was an error creating the URL
            e.printStackTrace();
            return false;
        }

        try {
            // Open a connection and query the project
            URLConnection conn = url.openConnection();

            // Add the user-agent to identify the program
            conn.addRequestProperty("User-Agent", "ServerModsAPI-Example (by Gravity)");

            // Read the response of the query
            // The response will be in a JSON format, so only reading one line is necessary.
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();

            // Parse the array of files from the query's response
            JSONArray array = (JSONArray)JSONValue.parse(response);

            if (array.size() > 0) {
                // Get the newest file's details
                JSONObject latest = (JSONObject)array.get(array.size() - 1);

                // Get the version's title
                String versionName = (String)latest.get(API_NAME_VALUE);

                // Compares the file name with the current version.
                latestVersion = versionName.replace("DirtyArrows [", "")
                        .replace("[a-z]", "")
                        .replace("[A-Z]", "")
                        .replace("]", "")
                        .replace("a", "")
                        .replace("b", "")
                        .replace("v", "");

                return !latestVersion.equalsIgnoreCase(currentVersion);
            }
            else {
                System.out.println("[DirtyArrows] There are no files for this project");
                return false;
            }
        }
        catch (IOException e) {
            // There was an error reading the query
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public String getLatestVersion() {
        return latestVersion;
    }
}