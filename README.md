Minimum viable product:<br>
    - We run a java program where we pass in a satellite image of BYU campus and your current location and desired destination.<br>
    - We can input how much you care about walking through lawns, parking lots, buildings, etc.<br>
    - This gives a shortest path from current location to desired destination.<br>
    - We will manually annotate building information such as hours, peak busy times, exits, elevators, etc.<br>
<br>
Desired product:<br>
    - Create a google maps web component where you can do the minimum viable product plus a simple web interface where 
    anybody can input current location and desired destination (only for BYU campus).<br>
    - Users can annotate buildings anywhere.<br>
    - Users can input how much they care about walking through lawns, parking lots, buildings, etc.<br>


Technologies used:<br>
    - Server side: Java<br>
    - Client side: Polymer<br>
    - Database: Firebase<br>
    
    
    
    
Ideas for the shortest Path:
* Generate nodes based on GMAPI to find paths that we KNOW exist
* Create a distance matrix based on euclidean distance (also can: create elevation matrix)
* Update distance matrix based on building data
* Update nodes for building exits / entrances
* Calculate Dijkstra's to find a shortest path, including ones that could go through obstacles
* Check if that path goes through an impossible obstacle, if so, update the weight to infinity and try again
* Check if that path goes through a lawn or parking lot, if so, update the weight based on preferences.
* Continue above until path cost does not improve.

