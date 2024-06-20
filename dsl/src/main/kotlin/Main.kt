package task
import java.io.File

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

const val ERROR_STATE = 0

interface ASTNode

data class City(val name: String, val coordinates: Coordinates, val commands: List<Command>) : ASTNode {
    override fun toString() = "City(name=$name, coordinates=$coordinates, commands=$commands)"
}

data class Coordinates(val latitude: Double, val longitude: Double) : ASTNode {
    override fun toString() = "Coordinates(latitude=$latitude, longitude=$longitude)"
}

sealed class Command : ASTNode

data class Temperature(val value: Double) : Command() {
    override fun toString() = "Temperature(value=$value)"
}

data class Wind(val speed: Double, val direction: String) : Command() {
    override fun toString() = "Wind(speed=$speed, direction=$direction)"
}

data class Precipitation(val value: Double) : Command() {
    override fun toString() = "Precipitation(value=$value)"
}

data class Pollution(val level: String) : Command() {
    override fun toString() = "Pollution(level=$level)"
}

data class Area(val name: String, val shapes: List<Shape>, val commands: List<Command>) : Command() {
    override fun toString() = "Area(name=$name, shapes=$shapes, commands=$commands)"
}

sealed class Shape : ASTNode

data class Polygon(val points: List<Coordinates>) : Shape() {
    override fun toString() = "Polygon(points=$points)"
}

data class Circle(val center: Coordinates, val radius: Double) : Shape() {
    override fun toString() = "Circle(center=$center, radius=$radius)"
}

enum class Symbol {
    CITY,
    CITY_NAME,
    COORDINATES,
    TEMPERATURE,
    WIND,
    PRECIPITATION,
    POLLUTION,
    AREA,
    AREA_NAME,
    POLYGON,
    CIRCLE,
    REAL,
    LPAREN,
    RPAREN,
    COMMA,
    CURLY_OPEN,
    CURLY_CLOSE,
    DIRECTION,
    POLLUTION_LEVEL,
    STRING,
    SKIP,
    EOF,
}

const val EOF = -1
const val NEWLINE = '\n'.code

interface DFA {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, code: Int): Int
    fun symbol(state: Int): Symbol
    val startState: Int
    val finalStates: Set<Int>
}

object Automaton : DFA {
    override val states = (1..77).toSet() // Adjusted states to include up to 76 based on the transitions needed
    override val alphabet = 0..255
    override val startState = 1
    override val finalStates = setOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77)

    private val numberOfStates = states.max() + 1 // plus the ERROR_STATE
    private val numberOfCodes = alphabet.max() + 1 // plus the EOF
    private val transitions = Array(numberOfStates) { IntArray(numberOfCodes) { ERROR_STATE } } // Initialized with ERROR_STATE
    private val values = Array(numberOfStates) { Symbol.SKIP }

    private fun setTransition(from: Int, chr: Char, to: Int) {
        transitions[from][chr.code + 1] = to // + 1 because EOF is -1 and the array starts at 0
    }

    private fun setTransition(from: Int, code: Int, to: Int) {
        transitions[from][code + 1] = to
    }

    private fun setSymbol(state: Int, symbol: Symbol) {
        values[state] = symbol
    }

    override fun next(state: Int, code: Int): Int {
        assert(states.contains(state))
        assert(alphabet.contains(code) || code == EOF)
        return transitions[state][code + 1]
    }

    override fun symbol(state: Int): Symbol {
        assert(states.contains(state))
        return values[state]
    }

    init {
        // Transitions sorted by state number
        setTransition(1, '(', 22)
        setTransition(1, ')', 23)
        setTransition(1, ',', 24)
        setTransition(1, '{', 25)
        setTransition(1, '}', 26)

        "city".forEachIndexed { index, char -> setTransition(1, char, 2 + index) }
        setTransition(5, ' ', 65)

        "temperature".forEachIndexed { index, char -> setTransition(1, char, 6 + index) }
        setTransition(17, ' ', 65)

        "wind".forEachIndexed { index, char -> setTransition(1, char, 18 + index) }
        setTransition(21, ' ', 65)

        "precipitation".forEachIndexed { index, char -> setTransition(1, char, 22 + index) }
        setTransition(35, ' ', 65)

        "pollution".forEachIndexed { index, char -> setTransition(1, char, 36 + index) }
        setTransition(45, ' ', 65)

        "area".forEachIndexed { index, char -> setTransition(1, char, 46 + index) }
        setTransition(50, ' ', 65)

        "polygon".forEachIndexed { index, char -> setTransition(1, char, 51 + index) }
        setTransition(58, ' ', 65)

        "circle".forEachIndexed { index, char -> setTransition(1, char, 59 + index) }
        setTransition(64, ' ', 65)

        ('0'..'9').forEach { char ->
            setTransition(1, char, 27)
            setTransition(27, char, 27)
            setTransition(28, char, 27)
        }
        setTransition(27, '.', 28)

        setTransition(1, '"', 29)
        (' '..'~').forEach { char ->
            setTransition(29, char, 29)
        }
        setTransition(29, '"', 30)

        setTransition(1, ' ', 65)
        setTransition(1, '\r', 66)
        setTransition(1, '\t', 67)
        setTransition(1, EOF, 68)

        setTransition(66, '\n', 1)

        // Handling direction and pollution regex
        setTransition(1, 'N', 69)
        setTransition(1, 'S', 69)
        setTransition(1, 'E', 69)
        setTransition(1, 'W', 69)
        "Moderate".forEachIndexed { index, char -> setTransition(1, char, 70 + index) }

        // Symbols
        setSymbol(5, Symbol.CITY)
        setSymbol(17, Symbol.TEMPERATURE)
        setSymbol(21, Symbol.WIND)
        setSymbol(35, Symbol.PRECIPITATION)
        setSymbol(45, Symbol.POLLUTION)
        setSymbol(50, Symbol.AREA)
        setSymbol(58, Symbol.POLYGON)
        setSymbol(64, Symbol.CIRCLE)

        setSymbol(22, Symbol.LPAREN)
        setSymbol(23, Symbol.RPAREN)
        setSymbol(24, Symbol.COMMA)
        setSymbol(25, Symbol.CURLY_OPEN)
        setSymbol(26, Symbol.CURLY_CLOSE)

        setSymbol(27, Symbol.REAL)
        setSymbol(28, Symbol.REAL)

        setSymbol(30, Symbol.STRING)
        setSymbol(69, Symbol.DIRECTION)
        setSymbol(75, Symbol.POLLUTION_LEVEL)
        setSymbol(68, Symbol.EOF)
    }
}

data class Token(val symbol: Symbol, val lexeme: String, val startRow: Int, val startColumn: Int)

class Scanner(private val automaton: DFA, private val stream: InputStream) {
    private var last: Int? = null
    private var row = 1
    private var column = 1

    private fun updatePosition(code: Int) {
        if (code == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    fun getToken(): Token {
        val startRow = row
        val startColumn = column
        val buffer = mutableListOf<Char>()

        var code = last ?: stream.read()
        var state = automaton.startState
        while (true) {
            val nextState = automaton.next(state, code)
            if (nextState == ERROR_STATE) break // Longest match

            state = nextState
            updatePosition(code)
            buffer.add(code.toChar())
            code = stream.read()
        }
        last = code // The code following the current lexeme is the first code of the next lexeme

        if (automaton.finalStates.contains(state)) {
            val symbol = automaton.symbol(state)
            return if (symbol == Symbol.SKIP) {
                getToken()
            } else {
                val lexeme = String(buffer.toCharArray())
                Token(symbol, lexeme, startRow, startColumn)
            }
        } else {
            throw Error("Invalid pattern at ${row}:${column}")
        }
    }
}

fun name(symbol: Symbol) =
    when (symbol) {
        Symbol.CITY -> "city"
        Symbol.CITY_NAME -> "city_name"
        Symbol.COORDINATES -> "coordinates"
        Symbol.TEMPERATURE -> "temperature"
        Symbol.WIND -> "wind"
        Symbol.PRECIPITATION -> "precipitation"
        Symbol.POLLUTION -> "pollution"
        Symbol.AREA -> "area"
        Symbol.AREA_NAME -> "area_name"
        Symbol.POLYGON -> "polygon"
        Symbol.CIRCLE -> "circle"
        Symbol.REAL -> "real"
        Symbol.LPAREN -> "lparen"
        Symbol.RPAREN -> "rparen"
        Symbol.COMMA -> "comma"
        Symbol.CURLY_OPEN -> "curly_open"
        Symbol.CURLY_CLOSE -> "curly_close"
        Symbol.DIRECTION -> "direction"
        Symbol.POLLUTION_LEVEL -> "pollution_level"
        Symbol.STRING -> "string"
        else -> throw Error("Invalid symbol")
    }

fun printTokens(scanner: Scanner, output: OutputStream) {
    val writer = output.writer(Charsets.UTF_8)

    var token = scanner.getToken()
    while (token.symbol != Symbol.EOF) {
        writer.append("${name(token.symbol)}(\"${token.lexeme}\") ") // The output ends with a space!
        token = scanner.getToken()
    }
    writer.appendLine()
    writer.flush()
}

class Recognizer(private val scanner: Scanner) {
    private var last: Token? = null

    fun recognizeStart(): City? {
        last = scanner.getToken()
        return recognizeCity()
    }

    private fun recognizeCity(): City? {
        return if (last?.symbol == Symbol.CITY) {
            recognizeTerminal(Symbol.CITY)
            val cityName = recognizeTerminal(Symbol.STRING)
            recognizeTerminal(Symbol.LPAREN)
            val coordinates = recognizeCoordinates()
            recognizeTerminal(Symbol.RPAREN)
            recognizeTerminal(Symbol.CURLY_OPEN)
            val commands = recognizeCommands()
            recognizeTerminal(Symbol.CURLY_CLOSE)
            City(cityName!!.lexeme, coordinates!!, commands)
        } else null
    }

    private fun recognizeCoordinates(): Coordinates? {
        val lat = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.COMMA)
        val lon = recognizeTerminal(Symbol.REAL)
        return if (lat != null && lon != null) {
            Coordinates(lat.lexeme.toDouble(), lon.lexeme.toDouble())
        } else null
    }

    private fun recognizeCommands(): List<Command> {
        val commands = mutableListOf<Command>()
        while (true) {
            val command = recognizeCommand()
            if (command != null) {
                commands.add(command)
            } else {
                break
            }
        }
        return commands
    }

    private fun recognizeCommand(): Command? {
        return when (last?.symbol) {
            Symbol.TEMPERATURE -> recognizeTemperature()
            Symbol.WIND -> recognizeWind()
            Symbol.PRECIPITATION -> recognizePrecipitation()
            Symbol.POLLUTION -> recognizePollution()
            Symbol.AREA -> recognizeArea()
            else -> null
        }
    }

    private fun recognizeTemperature(): Command? {
        recognizeTerminal(Symbol.TEMPERATURE)
        recognizeTerminal(Symbol.LPAREN)
        val value = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.RPAREN)
        return if (value != null) Temperature(value.lexeme.toDouble()) else null
    }

    private fun recognizeWind(): Command? {
        recognizeTerminal(Symbol.WIND)
        recognizeTerminal(Symbol.LPAREN)
        val speed = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.COMMA)
        val direction = recognizeTerminal(Symbol.DIRECTION)
        recognizeTerminal(Symbol.RPAREN)
        return if (speed != null && direction != null) Wind(speed.lexeme.toDouble(), direction.lexeme) else null
    }

    private fun recognizePrecipitation(): Command? {
        recognizeTerminal(Symbol.PRECIPITATION)
        recognizeTerminal(Symbol.LPAREN)
        val value = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.RPAREN)
        return if (value != null) Precipitation(value.lexeme.toDouble()) else null
    }

    private fun recognizePollution(): Command? {
        recognizeTerminal(Symbol.POLLUTION)
        recognizeTerminal(Symbol.LPAREN)
        val level = recognizeTerminal(Symbol.POLLUTION_LEVEL)
        recognizeTerminal(Symbol.RPAREN)
        return if (level != null) Pollution(level.lexeme) else null
    }

    private fun recognizeArea(): Command? {
        recognizeTerminal(Symbol.AREA)
        val areaName = recognizeTerminal(Symbol.STRING)
        recognizeTerminal(Symbol.CURLY_OPEN)
        val shapes = recognizeShapes()
        val commands = recognizeCommands()
        recognizeTerminal(Symbol.CURLY_CLOSE)
        return if (areaName != null) Area(areaName.lexeme, shapes, commands) else null
    }

    private fun recognizeShapes(): List<Shape> {
        val shapes = mutableListOf<Shape>()
        while (true) {
            val shape = recognizeShape()
            if (shape != null) {
                shapes.add(shape)
            } else {
                break
            }
        }
        return shapes
    }

    private fun recognizeShape(): Shape? {
        return when (last?.symbol) {
            Symbol.POLYGON -> recognizePolygon()
            Symbol.CIRCLE -> recognizeCircle()
            else -> null
        }
    }

    private fun recognizePolygon(): Shape? {
        recognizeTerminal(Symbol.POLYGON)
        recognizeTerminal(Symbol.LPAREN)
        val points = recognizePoints()
        recognizeTerminal(Symbol.RPAREN)
        return if (points.isNotEmpty()) Polygon(points) else null
    }

    private fun recognizeCircle(): Shape? {
        recognizeTerminal(Symbol.CIRCLE)
        recognizeTerminal(Symbol.LPAREN)
        val centerX = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.COMMA)
        val centerY = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.COMMA)
        val radius = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.RPAREN)
        return if (centerX != null && centerY != null && radius != null) {
            Circle(Coordinates(centerX.lexeme.toDouble(), centerY.lexeme.toDouble()), radius.lexeme.toDouble())
        } else null
    }

    private fun recognizePoints(): List<Coordinates> {
        val points = mutableListOf<Coordinates>()
        while (true) {
            val point = recognizePoint()
            if (point != null) {
                points.add(point)
            } else {
                break
            }
        }
        return points
    }

    private fun recognizePoint(): Coordinates? {
        recognizeTerminal(Symbol.LPAREN)
        val lat = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.COMMA)
        val lon = recognizeTerminal(Symbol.REAL)
        recognizeTerminal(Symbol.RPAREN)
        return if (lat != null && lon != null) {
            Coordinates(lat.lexeme.toDouble(), lon.lexeme.toDouble())
        } else null
    }

    private fun recognizeTerminal(symbol: Symbol): Token? {
        return if (last?.symbol == symbol) {
            val token = last
            last = scanner.getToken()
            token
        } else {
            null
        }
    }
}

fun main(args: Array<String>) {
    val inputFile = FileInputStream(args[0])
    val outputFile = FileOutputStream(args[1])

    printTokens(Scanner(Automaton, inputFile), outputFile)

    val writer = outputFile.writer(Charsets.UTF_8)
    val recog = Recognizer(Scanner(Automaton, inputFile))
    val city = recog.recognizeStart()
    writer.write(
        when (city) {
            null -> "reject"
            else -> "accept\n$city"
        }
    )
    writer.flush()
    println("Done!")

    inputFile.close()
    outputFile.close()

    val fileContent = File(args[1]).readText()
    println(fileContent)
}
