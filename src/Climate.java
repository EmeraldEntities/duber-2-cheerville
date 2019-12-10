/** Class: Climate
 * @version 1.2
 * @author Joseph Wang
 * @date 12/2/2019
 * @description: The class that controls everything to do with climate, weather and temperature.
 */

class Climate {
    //- Cooldowns and current weather storers. Made to work with the final variables for indexing.
    private int weatherCooldown, currentWeather, seasonCooldown, currentSeason;

    private final int MIN_WEATHER_TURNS;
    private final int SEASON_TURNS;

    //- Final variables designed to make array indexing easier to read
    private final int SUNNY = 0;
    private final int CLOUDY = 1;
    private final int RAINY = 2;

    private final int SPRING = 0;
    private final int SUMMER = 1;
    private final int FALL = 2;
    private final int WINTER = 3;

    //- Predetermined values with how much each factor affects grow speed.
    private int[] weatherFactors = {6, 4, 10};
    private int[] temperatureFactors = {4, 2, 3, -2};

    /** Climate(minWeatherTurns, seasonTurns); 
     * A constructor for a new climate. Takes in the minimum amount of turns for weather and season, and
     * stores them in class variables. Also sets up cooldowns and starting season.
     * @param minWeatherTurns, an int with the minimum amount of turns a weather cycle can last.
     * @param seasonTurns, an int with the minimum amount of turns a season can last.
     */
    Climate(int minWeatherTurns, int seasonTurns) {
        this.MIN_WEATHER_TURNS = minWeatherTurns;
        this.SEASON_TURNS = seasonTurns;

        this.weatherCooldown = 0;
        this.currentSeason = SPRING;
        this.seasonCooldown = SEASON_TURNS;
    }

    /** getWeather(); 
     * Returns the current weather.
     * @return a string with the current weather.
     */
    public String getWeather() {
        if (currentWeather == SUNNY) {
            return "Sunny";
        } else if (currentWeather == CLOUDY) {
            return "Cloudy";
        } else {
            return "Rainy";
        }
    }

    /** getSeason(); 
     * Returns the current season.
     * @return a string with the current season.
     */
    public String getSeason() {
        if (currentSeason == SPRING) {
            return "Spring";
        } else if (currentSeason == SUMMER) {
            return "Summer";
        } else if (currentSeason == FALL) {
            return "Fall";
        } else {
            return "Winter";
        }
    }

    /** changeGrowSpeed(defaultValue); 
     * Calculate the effects of both weather and season on the grow speed, adds the default value, and returns it.
     * @param defaultValue, an int with the absolute minimum amount of plants growing in the world.
     * @return an int with the new grow speed.
     */
    public int changeGrowSpeed(int defaultValue) {
        return defaultValue + calculateWeather() + calculateSeason();
    };

    /** calculateWeather(); 
     * Calculate the effects of weather on growspeed, or changes the weather randomly if it's been long enough.
     * @return an int with the effects of weather on grow speed.
     */
    private int calculateWeather() {
        if (this.weatherCooldown == 0) {
            int newWeather;
            do {
                newWeather = Simulation.randInt(0, weatherFactors.length);
            } while(newWeather == this.currentWeather);

            this.currentWeather = newWeather;
            this.weatherCooldown = this.MIN_WEATHER_TURNS + generateWeatherVariation();
        } else {
            this.weatherCooldown--;
        }

        return this.weatherFactors[this.currentWeather];
    }

    /** calculateSeason(); 
     * Calculate the effects of the season on growspeed, or changes the season to the next one if it's been long enough.
     * @return an int with the effects of season on grow speed.
     */
    private int calculateSeason() {
        if (this.seasonCooldown == 0) {
            currentSeason = (currentSeason + 1) % 4;
            this.seasonCooldown = SEASON_TURNS;
            
        } else {
            this.seasonCooldown--;
        }

        return temperatureFactors[currentSeason];
    }

    /** generateWeatherVariation(); 
     * Returns a small offset for the weather so it doesn't last too long.
     * @return a random int from -10 to 10.
     */
    private int generateWeatherVariation() {
        return Simulation.randInt(-10, 10);
      }

}