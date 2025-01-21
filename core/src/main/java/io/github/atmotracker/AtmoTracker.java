package io.github.atmotracker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.atmotracker.markers.AirQualityMarker;
import io.github.atmotracker.markers.WeatherMarker;
import io.github.atmotracker.utility.Constants;
import io.github.atmotracker.utility.Geolocation;
import io.github.atmotracker.utility.MapRasterTiles;
import io.github.atmotracker.utility.MarkerType;
import io.github.atmotracker.utility.ZoomXY;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AtmoTracker extends ApplicationAdapter implements GestureDetector.GestureListener {

    private ShapeRenderer shapeRenderer;

    private SpriteBatch spriteBatch;
    private Vector3 touchPosition;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;

    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile

    private List<WeatherMarker> weatherMarkers = new ArrayList<>(); //markers for the weatherData
    private List<AirQualityMarker> airQualityMarkers = new ArrayList<>();
    private MarkerType currentMarkerType = MarkerType.WEATHER;
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.1512, 14.9955);
    private ParticleEffect fireEffect;

    private ParticleEffect smokeEffect;
    private Map<Geolocation, ParticleEffect> fireMarkers = new HashMap<>();

    private BitmapFont font;

    private Stage stage;
    private Skin skin;
    // test marker


    @Override
    public void create() {


        fetchWeatherData();
        fetchAirQualityData();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("particles/fire.p"), Gdx.files.internal("particles"));
        smokeEffect = new ParticleEffect();
        smokeEffect.load(Gdx.files.internal("particles/smoke.p"), Gdx.files.internal("particles"));


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.viewportWidth = Constants.MAP_WIDTH / 2f;
        camera.viewportHeight = Constants.MAP_HEIGHT / 2f;
        camera.zoom = 1.9f;
        camera.update();

        touchPosition = new Vector3();

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, Constants.ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, Constants.NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(Constants.ZOOM, centerTile.x - ((Constants.NUM_TILES - 1) / 2), centerTile.y - ((Constants.NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(Constants.NUM_TILES, Constants.NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = Constants.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < Constants.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skins2/comic-ui.json"));
        Gdx.input.setInputProcessor(stage);
        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gestureDetector));
    }

    private void fetchWeatherData() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://localhost:3001/data/allWeather");
        request.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    String jsonResponse = httpResponse.getResultAsString();
                    parseWeatherData(jsonResponse);
                } else {
                     Gdx.app.error("HTTP", "Failed to fetch weather data: " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("HTTP", "Failed to fetch weather data: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });
    }
    private void fetchAirQualityData() {
         Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://localhost:3001/data/allAirQuality");
        request.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    String jsonResponse = httpResponse.getResultAsString();
                    parseAirQualityData(jsonResponse);
                } else {
                    Gdx.app.error("HTTP", "Failed to fetch weather data: " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("HTTP", "Failed to fetch weather data: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });
    }
    private void parseWeatherData(String jsonResponse) {
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(jsonResponse);

        weatherMarkers.clear(); // Clear existing markers
        for (JsonValue entry : root) {
            float latitude = entry.get("location").getFloat("latitude", 0);  // Replace with your actual field name
            float longitude = entry.get("location").getFloat("longitude", 0); // Replace with your actual field name
            float temperature = entry.getFloat("temperature", 0);
            String name = entry.getString("name", "Unknown");
            float windSpeed = entry.getFloat("windSpeed", 0);
            float windGusts = entry.getFloat("windGusts",0);
            float precipitation = entry.getFloat("precipitation",0);
            if (latitude != 0 && longitude != 0) {
                weatherMarkers.add(new WeatherMarker(
                    new Geolocation(latitude, longitude),
                    name,
                    temperature,
                    windSpeed,
                    windGusts,
                    precipitation
                ));


                System.out.println( "Temp=" + temperature + ", Name=" + name + "windspeed "+  windSpeed + " windgust "+  windGusts + " precipitation " + precipitation);
            }
        }

    }
    private void parseAirQualityData(String jsonResponse) {
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(jsonResponse);

        airQualityMarkers.clear(); // Clear existing markers
        for (JsonValue entry : root) {
            float latitude = entry.get("location").getFloat("latitude", 0);
            float longitude = entry.get("location").getFloat("longitude", 0);
            String name = entry.getString("name", "Unknown");
            float pm10 = safeParseFloat(entry.getString("pm10", "0"));
            float pm25 = safeParseFloat(entry.getString("pm25", "0"));
            float so2 = safeParseFloat(entry.getString("so2", "0"));
            float co = safeParseFloat(entry.getString("co", "0"));
            float ozon = safeParseFloat(entry.getString("ozon", "0"));
            float no2 = safeParseFloat(entry.getString("no2", "0"));
            float benzen = safeParseFloat(entry.getString("benzen", "0"));
            if (latitude != 0 && longitude != 0) {
                airQualityMarkers.add(new AirQualityMarker(
                    new Geolocation(latitude, longitude),
                    name,
                    pm10,
                    pm25,
                    so2,
                    co,
                    ozon,
                    no2,
                    benzen
                ));
                //System.out.println(  jsonResponse);
            }
        }
    }

    private float safeParseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0; // Default value for invalid floats
        }
    }
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        handleInput();

        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();


        if (currentMarkerType == MarkerType.WEATHER) {
            drawWeatherDataMarkers();
        } else if (currentMarkerType == MarkerType.AIR_QUALITY) {
            drawAirQualityDataMarkers();
        }


        SpriteBatch batch = new SpriteBatch();
        batch.begin();

        batch.end();

        super.render();

        // Render current marker type as a label
        batch.begin();
        font.draw(batch, "Current Mode: " + currentMarkerType, 10, Gdx.graphics.getHeight() - 10);
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void drawWeatherDataMarkers() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (WeatherMarker marker : weatherMarkers) {
            Vector2 markerPosition = MapRasterTiles.getPixelPosition(marker.location.lat, marker.location.lng, beginTile.x, beginTile.y);
            shapeRenderer.circle(markerPosition.x, markerPosition.y, 10);

        }
        shapeRenderer.end();



    }
    private void drawAirQualityDataMarkers() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (AirQualityMarker  marker : airQualityMarkers) {
            Vector2 markerPosition = MapRasterTiles.getPixelPosition(marker.location.lat, marker.location.lng, beginTile.x, beginTile.y);
            shapeRenderer.circle(markerPosition.x, markerPosition.y, 10);
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);

       for (WeatherMarker marker : weatherMarkers) {
            Vector2 markerPosition = MapRasterTiles.getPixelPosition(marker.location.lat, marker.location.lng, beginTile.x, beginTile.y);
            if (markerPosition.dst(touchPosition.x, touchPosition.y) < 15) {
                showWeatherDetails(marker);
                break;
           }
        }

        for (AirQualityMarker marker : airQualityMarkers) {
            Vector2 markerPosition = MapRasterTiles.getPixelPosition(marker.location.lat, marker.location.lng, beginTile.x, beginTile.y);
            if (markerPosition.dst(touchPosition.x, touchPosition.y) < 7) {
                showAirQualityDetails(marker);
                return true;
            }
        }

        return false;
    }



    private void showWeatherDetails(WeatherMarker marker) {
        stage.clear();



        Window window = new Window("", skin);
        window.setMovable(false);
        window.setResizable(false);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label(marker.name, skin, "title");
        Label temperature = new Label("Temperature: " + marker.temperature + " °C", skin);
        Label windSpeed = new Label("Wind Speed: " + marker.windSpeed + " m/s", skin);
        Label precipitation = new Label("Precipitation: " + marker.precipitation + " mm", skin);
        Label windGusts = new Label("Wind Gusts: " + marker.windGusts + " m/s", skin);


        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.clear();
            }
        });

        table.add(title).pad(10).row();
        table.add(temperature).pad(5).row();
        table.add(windSpeed).pad(5).row();
        table.add(windGusts).pad(5).row();
        table.add(precipitation).pad(5).row();
        table.add(closeButton).pad(10);
        window.add(table);
        window.pack();
        window.setPosition(
            (800- window.getWidth()) / 2,
            (1600 - window.getHeight()) / 2
        );
        stage.addActor(window);
    }


    private void showAirQualityDetails(AirQualityMarker marker) {
        stage.clear();

        Window window = new Window("", skin);
        window.setMovable(false);
        window.setResizable(false);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label(marker.name, skin, "title");
        Label pm10 = new Label("PM10: " + marker.pm10 + " µg/m³", skin);
        Label pm25 = new Label("PM2.5: " + marker.pm25 + " µg/m³", skin);
        Label so2 = new Label("SO2: " + marker.so2 + " µg/m³", skin);
        Label co = new Label("CO: " + marker.co + " µg/m³", skin);
        Label ozon = new Label("Ozon: " + marker.ozon + " µg/m³", skin);
        Label no2 = new Label("NO2: " + marker.no2 + " µg/m³", skin);
        Label benzen = new Label("Benzen: " + marker.benzen + " µg/m³", skin);

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.clear();
            }
        });

        table.add(title).pad(10).row();
        table.add(pm10).pad(5).row();
        table.add(pm25).pad(5).row();
        table.add(so2).pad(5).row();
        table.add(co).pad(5).row();
        table.add(ozon).pad(5).row();
        table.add(no2).pad(5).row();
        table.add(benzen).pad(5).row();
        table.add(closeButton).pad(10);

        window.add(table);
        window.pack();
        window.setPosition(
            (800- window.getWidth()) / 2,
            (1600 - window.getHeight()) / 2
        );
        stage.addActor(window);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);


        return false; // No marker was tapped
    }


    private void drawModeIndicator(SpriteBatch batch, BitmapFont font) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        String modeText = "Current Mode: " + currentMarkerType;

        // Calculate dimensions
        float padding = 10;
        float fontWidth = font.getRegion().getRegionWidth();
        float fontHeight = font.getLineHeight();
        float windowWidth = fontWidth + padding * 2;
        float windowHeight = fontHeight + padding * 2;

        // Window position (bottom-left corner)
        float windowX = 10;
        float windowY = 10;

        // Draw the background rectangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // Semi-transparent black
        shapeRenderer.rect(windowX, windowY, windowWidth, windowHeight);
        shapeRenderer.end();

        // Draw the border (optional)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1); // White border
        shapeRenderer.rect(windowX, windowY, windowWidth, windowHeight);
        shapeRenderer.end();

        // Render the text
        batch.begin();
        font.draw(batch, modeText, windowX + padding, windowY + windowHeight - padding);
        batch.end();

        shapeRenderer.dispose();
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) { // Press 'T' to toggle marker type
            if (currentMarkerType == MarkerType.WEATHER) {
                currentMarkerType = MarkerType.AIR_QUALITY;
            } else {
                currentMarkerType = MarkerType.WEATHER;
            }
            Gdx.app.log("Marker Toggle", "Switched to " + currentMarkerType + " markers.");
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }
}
