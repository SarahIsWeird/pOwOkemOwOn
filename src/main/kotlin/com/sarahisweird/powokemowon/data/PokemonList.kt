package com.sarahisweird.powokemowon.data

class PokemonList(data: Sequence<Map<String, String>>, data2: List<Map<String, String>>) {
    private val list: List<PokemonData>
    private val nameIdMap: Map<String, Int>

    init {
        val tempList = mutableListOf<PokemonData>()
        val germanMap = data2.associate { Pair(it["name"]!!.lowercase(), it["german_name"]) }

        data.forEach { row ->
            tempList += PokemonData(
                germanName = if (germanMap.containsKey(row["name"]!!.lowercase()))
                    germanMap[row["name"]!!.lowercase()]!! else row["name"]!!,
                japaneseName = row["japanese_name"]!!,
                name = row["name"]!!,
                pokedexNumber = row["pokedex_number"]!!.toInt()
            )
        }

        list = tempList
        nameIdMap = list.associate { Pair(it.name.lowercase(), it.pokedexNumber) }
    }

    fun findByName(name: String): PokemonData? =
        // Make sure index can go out of bounds if it doesn't exist
        list.getOrNull((nameIdMap[name.lowercase()] ?: 0) - 1)

    fun findByPokedexId(id: Int): PokemonData? =
        list.getOrNull(id - 1)

    fun randomPokemon(): PokemonData =
        list.random()
}