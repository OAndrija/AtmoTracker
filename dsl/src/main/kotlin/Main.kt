package task

import java.io.*

const val ERROR_STATE = 0

enum class Symbol {
    CITY,
    CITY_NAME,
    REAL,
    TEMPERATURE,
    WIND,
    PRECIPITATION,
    POLLUTION,
    POLLUTION_REGEX,
    AREA,
    AREA_NAME,
    SHAPE,
    POLYGON,
    CIRCLE,
    POINT,
    DIRECTION_REGEX,
    COMMA,
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    QUOTE,
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
    override val states = (1..30).toSet()
    override val alphabet = 0..255
    override val startState = 1
    override val finalStates = setOf(
        2, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 16, 18, 21, 23, 25, 26, 27, 28, 29, 30
    )

    private val numberOfStates = states.max() + 1 // plus the ERROR_STATE
    private val numberOfCodes = alphabet.max() + 1 // plus the EOF
    private val transitions = Array(numberOfStates) { IntArray(numberOfCodes) }
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
        assert(alphabet.contains(code))
        return transitions[state][code + 1]
    }

    override fun symbol(state: Int): Symbol {
        assert(states.contains(state))
        return values[state]
    }

    init {
        // Real numbers
        ('0'..'9').forEach { char ->
            setTransition(1, char, 2)
            setTransition(2, char, 2)
            setTransition(3, char, 4)
            setTransition(4, char, 4)
            setTransition(5, char, 5)
        }
        setTransition(2, '.', 3)

        // Variable names (for CITY-NAME and AREA-NAME)
        (('a'..'z') + ('A'..'Z')).forEach { char ->
            setTransition(1, char, 6)
            setTransition(6, char, 6)
        }

        // Strings for CITY-NAME and AREA-NAME
        setTransition(1, '"', 7)
        (0..255).forEach { code -> setTransition(7, code, 7) } // Any character inside quotes
        setTransition(7, '"', 8)

        // Keywords and symbols
        "city".forEachIndexed { i, c -> setTransition(i + 1, c, i + 2) }
        setTransition(5, ' ', 9)

        "temperature".forEachIndexed { i, c -> setTransition(i + 1, c, i + 2) }
        setTransition(12, '(', 13)

        "wind".forEachIndexed { i, c -> setTransition(i + 1, c, i + 2) }
        setTransition(16, '(', 17)

        "precipitation".forEachIndexed { i, c -> setTransition(i + 1, c, i + 2) }
        setTransition(20, '(', 21)

        "pollution".forEachIndexed { i, c -> setTransition(i + 1, c, i + 2) }
        setTransition(24, '(', 25)

        "area".forEachIndexed { i, c -> setTransition(i + 1, c, i + 2) }
        setTransition(4, ' ', 6)

        setTransition(1, ',', 10)
        setTransition(1, '(', 11)
        setTransition(1, ')', 12)
        setTransition(1, '{', 13)
        setTransition(1, '}', 14)
        setTransition(1, EOF, 15)

        setSymbol(2, Symbol.REAL)
        setSymbol(4, Symbol.REAL)
        setSymbol(6, Symbol.STRING)
        setSymbol(7, Symbol.QUOTE)
        setSymbol(8, Symbol.CITY_NAME)
        setSymbol(9, Symbol.CITY)
        setSymbol(10, Symbol.COMMA)
        setSymbol(11, Symbol.LPAREN)
        setSymbol(12, Symbol.RPAREN)
        setSymbol(13, Symbol.LBRACE)
        setSymbol(14, Symbol.RBRACE)
        setSymbol(15, Symbol.EOF)
        setSymbol(16, Symbol.TEMPERATURE)
        setSymbol(17, Symbol.LPAREN)
        setSymbol(18, Symbol.WIND)
        setSymbol(21, Symbol.PRECIPITATION)
        setSymbol(24, Symbol.POLLUTION)
        setSymbol(25, Symbol.POLLUTION_REGEX)
        setSymbol(26, Symbol.AREA)
        setSymbol(28, Symbol.DIRECTION_REGEX)
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
        Symbol.CITY_NAME -> "city-name"
        Symbol.REAL -> "real"
        Symbol.TEMPERATURE -> "temperature"
        Symbol.WIND -> "wind"
        Symbol.PRECIPITATION -> "precipitation"
        Symbol.POLLUTION -> "pollution"
        Symbol.POLLUTION_REGEX -> "pollution-regex"
        Symbol.AREA -> "area"
        Symbol.AREA_NAME -> "area-name"
        Symbol.SHAPE -> "shape"
        Symbol.POLYGON -> "polygon"
        Symbol.CIRCLE -> "circle"
        Symbol.POINT -> "point"
        Symbol.DIRECTION_REGEX -> "direction-regex"
        Symbol.COMMA -> "comma"
        Symbol.LPAREN -> "lparen"
        Symbol.RPAREN -> "rparen"
        Symbol.LBRACE -> "lbrace"
        Symbol.RBRACE -> "rbrace"
        Symbol.QUOTE -> "quote"
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

fun main(args: Array<String>) {
    val inputFile = FileInputStream(args[0])
    val outputFile = FileOutputStream(args[1])
    printTokens(Scanner(Automaton, inputFile), outputFile)
}