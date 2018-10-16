package com.appersiano.testgetaroundtmdb.repository.model

data class MoviesResponse(
        val dates: Dates? = null,
        val page: Int? = null,
        val totalPages: Int? = null,
        val results: List<Movie?>? = null,
        val totalResults: Int? = null
)

data class Dates(
		/*
		We should create a custom serializer/deserializer to get a Date type,
		I leave it as String because I do not use it the project
		*/
		val maximum: String? = null,
		val minimum: String? = null
)

data class Movie(
		val overview: String? = null,
		val original_language: String? = null,
		val original_title: String? = null,
		val video: Boolean? = null,
		val title: String? = null,
		val genreIds: List<Int?>? = null,
		val poster_path: String? = null,
		val backdrop_path: String? = null,
		val release_date: String? = null,
		val vote_average: Double? = null,
		val popularity: Double? = null,
		val id: Int? = null,
		val adult: Boolean? = null,
		val vote_count: Int? = null
)
