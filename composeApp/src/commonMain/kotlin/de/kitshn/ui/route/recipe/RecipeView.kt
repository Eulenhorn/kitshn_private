package de.kitshn.ui.route.recipe

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.savedstate.read
import de.kitshn.api.tandoor.rememberTandoorRequestState
import de.kitshn.ui.TandoorRequestErrorHandler
import de.kitshn.ui.component.alert.FullSizeAlertPane
import de.kitshn.ui.component.buttons.BackButton
import de.kitshn.ui.route.RouteParameters
import de.kitshn.ui.view.ViewParameters
import de.kitshn.ui.view.recipe.details.ViewRecipeDetails
import kitshn.composeapp.generated.resources.Res
import kitshn.composeapp.generated.resources.recipe_not_found
import org.jetbrains.compose.resources.stringResource

@Composable
fun RouteRecipeView(
    p: RouteParameters
) {
    val recipeId = p.bse.arguments?.read { getString("recipeId") }
    if(recipeId == null) {
        FullSizeAlertPane(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = stringResource(Res.string.recipe_not_found),
            text = stringResource(Res.string.recipe_not_found)
        )

        return
    }

    val client = p.vm.tandoorClient ?: return

    // retrieve shared recipe
    val requestState = rememberTandoorRequestState()
    LaunchedEffect(recipeId) {
        requestState.wrapRequest {
            val recipe = client.recipe.get(id = recipeId.toInt())
            client.container.recipe[recipe.id] = recipe
            client.container.recipeOverview[recipe.id] = recipe.toOverview()
        }
    }

    ViewRecipeDetails(
        p = ViewParameters(p.vm, p.onBack),

        navigationIcon = {
            BackButton(onBack = p.onBack, overlay = true)
        },

        recipeId = recipeId.toInt(),
        client = client,

        onClickUser = {
            p.vm.searchCreatedBy(id = it.id)
        },

        onClickKeyword = {
            p.vm.searchKeyword(id = it.id)
        }
    )

    TandoorRequestErrorHandler(requestState)
}