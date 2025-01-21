package com.example.atmotracker.model

class BlockchainData( val index: Int,
                      val timestamp: String,
                      val data: String,
                      val previousHash: String,
                      val hash: String,
                      val difficulty: Int,
                      val token: Int) {
}