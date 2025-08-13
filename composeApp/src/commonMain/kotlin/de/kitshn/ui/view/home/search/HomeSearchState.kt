package de.kitshn.ui.view.home.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.kitshn.api.tandoor.TandoorClient
import de.kitshn.api.tandoor.TandoorRequestState
import de.kitshn.api.tandoor.model.TandoorFood
import de.kitshn.api.tandoor.model.TandoorKeyword
import de.kitshn.api.tandoor.model.TandoorKeywordOverview
import de.kitshn.api.tandoor.route.TandoorRecipeQueryParametersSortOrder
import de.kitshn.api.tandoor.route.TandoorUser
import de.kitshn.ui.component.search.AdditionalSearchSettingsChipRowState
import de.kitshn.ui.state.foreverRememberNotSavable
import kotlinx.coroutines.delay

@Composable
fun rememberHomeSearchState(
    key: String
): MutableState<HomeSearchState> {
    return foreverRememberNotSavable(
        key = key,
        initialValue = HomeSearchState()
    )
}

data class HomeSearchStateDefaultValues(
    val query: String = "",
    val new: Boolean = false,
    val random: Boolean = false,
    val keywords: List<TandoorKeyword> = listOf(),
    val foods: List<TandoorFood> = listOf(),
    val createdBy: TandoorUser? = null,
    val minimumRating: Int? = null,
    val sortOrder: TandoorRecipeQueryParametersSortOrder? = null
)

class HomeSearchState(
    val shown: MutableState<Boolean> = mutableStateOf(false)
) {

    var searchRequestState = TandoorRequestState()
    var extendedSearchRequestState = TandoorRequestState()

    var query by mutableStateOf("")
    var additionalSearchSettingsChipRowState = AdditionalSearchSettingsChipRowState()

    val searchResultIds = mutableStateListOf<Int>()
    var currentPage by mutableIntStateOf(1)
    var nextPageExists by mutableStateOf(false)

    var defaultValuesApplied: Boolean = true
    var defaultValues = HomeSearchStateDefaultValues()

    var appliedAutoFocusSearchField: Boolean = false

    fun open() {
        this.appliedAutoFocusSearchField = false

        this.defaultValues = HomeSearchStateDefaultValues()
        this.shown.value = true
    }

    fun open(values: HomeSearchStateDefaultValues) {
        this.appliedAutoFocusSearchField = false

        this.defaultValuesApplied = false
        this.defaultValues = values
        this.shown.value = true
    }

    suspend fun openWithKeyword(client: TandoorClient, keywordOverview: TandoorKeywordOverview) {
        return openWithKeywordId(client, keywordOverview.id)
    }

    suspend fun openWithKeywordId(client: TandoorClient, keywordId: Int) {
        TandoorRequestState().wrapRequest {
            val keyword = client.container.keyword[keywordId]
                ?: client.keyword.retrieve(keywordId)

            open(
                HomeSearchStateDefaultValues(
                    keywords = listOf(keyword)
                )
            )
        }
    }

    fun openWithCreatedBy(user: TandoorUser) {
        open(
            HomeSearchStateDefaultValues(
                createdBy = user
            )
        )
    }

    suspend fun openWithCreatedById(client: TandoorClient, userId: Int) {
        TandoorRequestState().wrapRequest {
            val user = client.user.getUsers().find { it.id == userId }

            open(
                HomeSearchStateDefaultValues(
                    createdBy = user
                )
            )
        }
    }

    fun dismiss() {
        this.shown.value = false
    }

    suspend fun reopen(open: suspend () -> Unit) {
        if(this.shown.value) {
            dismiss()
            delay(250)
        }

        open()
    }

}