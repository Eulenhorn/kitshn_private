package de.kitshn.ui.view.recipe.details

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Reviews
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import de.kitshn.KITSHN_KEYWORD_FLAG_PREFIX
import de.kitshn.KITSHN_KEYWORD_FLAG__HIDE_INGREDIENT_ALLOCATION_ACTION_CHIP
import de.kitshn.KITSHN_KEYWORD_FLAG__HIDE_INGREDIENT_ALLOCATION_ACTION_CHIP_DESC
import de.kitshn.api.tandoor.TandoorClient
import de.kitshn.api.tandoor.TandoorRequestState
import de.kitshn.api.tandoor.model.TandoorIngredient
import de.kitshn.api.tandoor.model.TandoorKeywordOverview
import de.kitshn.api.tandoor.model.TandoorStep
import de.kitshn.api.tandoor.model.recipe.TandoorRecipe
import de.kitshn.api.tandoor.rememberTandoorRequestState
import de.kitshn.formatDuration
import de.kitshn.handleTandoorRequestState
import de.kitshn.launchTimerHandler
import de.kitshn.launchWebsiteHandler
import de.kitshn.shareContentHandler
import de.kitshn.ui.TandoorRequestErrorHandler
import de.kitshn.ui.component.alert.FullSizeAlertPane
import de.kitshn.ui.component.buttons.BackButton
import de.kitshn.ui.component.buttons.WideActionChip
import de.kitshn.ui.component.buttons.WideActionChipType
import de.kitshn.ui.component.icons.FiveStarIconRow
import de.kitshn.ui.component.icons.IconWithState
import de.kitshn.ui.component.model.ingredient.IngredientsList
import de.kitshn.ui.component.model.recipe.RecipeInfoBlob
import de.kitshn.ui.component.model.recipe.RecipePropertiesCard
import de.kitshn.ui.component.model.recipe.activity.RecipeActivityPreviewCard
import de.kitshn.ui.component.model.recipe.button.RecipeFavoriteButton
import de.kitshn.ui.component.model.recipe.step.RecipeStepCard
import de.kitshn.ui.component.model.servings.ServingsSelector
import de.kitshn.ui.dialog.LaunchTimerInfoBottomSheet
import de.kitshn.ui.dialog.UseShareWrapperDialog
import de.kitshn.ui.dialog.common.CommonDeletionDialog
import de.kitshn.ui.dialog.common.rememberCommonDeletionDialogState
import de.kitshn.ui.dialog.mealplan.MealPlanCreationAndEditDefaultValues
import de.kitshn.ui.dialog.mealplan.MealPlanCreationAndEditDialog
import de.kitshn.ui.dialog.mealplan.rememberMealPlanCreationDialogState
import de.kitshn.ui.dialog.recipe.RecipeActivitiesBottomSheet
import de.kitshn.ui.dialog.recipe.RecipeAddToShoppingDialog
import de.kitshn.ui.dialog.recipe.RecipeIngredientAllocationDialog
import de.kitshn.ui.dialog.recipe.RecipeLinkDialog
import de.kitshn.ui.dialog.recipe.creationandedit.RecipeCreationAndEditDialog
import de.kitshn.ui.dialog.recipe.creationandedit.rememberRecipeEditDialogState
import de.kitshn.ui.dialog.recipe.rememberRecipeActivitiesBottomSheetState
import de.kitshn.ui.dialog.recipe.rememberRecipeAddToShoppingDialogState
import de.kitshn.ui.dialog.recipe.rememberRecipeIngredientAllocationDialogState
import de.kitshn.ui.dialog.recipe.rememberRecipeLinkDialogState
import de.kitshn.ui.dialog.recipeBook.ManageRecipeInRecipeBooksDialog
import de.kitshn.ui.dialog.recipeBook.rememberManageRecipeInRecipeBooksDialogState
import de.kitshn.ui.dialog.rememberLaunchTimerInfoBottomSheetState
import de.kitshn.ui.dialog.rememberUseShareWrapperDialogState
import de.kitshn.ui.layout.ResponsiveSideBySideLayout
import de.kitshn.ui.state.ErrorLoadingSuccessState
import de.kitshn.ui.state.rememberErrorLoadingSuccessState
import de.kitshn.ui.theme.Typography
import de.kitshn.ui.view.ViewParameters
import kitshn.composeApp.BuildConfig
import kitshn.composeapp.generated.resources.Res
import kitshn.composeapp.generated.resources.action_close
import kitshn.composeapp.generated.resources.action_delete
import kitshn.composeapp.generated.resources.action_edit
import kitshn.composeapp.generated.resources.action_more
import kitshn.composeapp.generated.resources.action_open_original
import kitshn.composeapp.generated.resources.action_open_source
import kitshn.composeapp.generated.resources.action_share
import kitshn.composeapp.generated.resources.action_start_cooking
import kitshn.composeapp.generated.resources.common_okay
import kitshn.composeapp.generated.resources.common_review
import kitshn.composeapp.generated.resources.common_source
import kitshn.composeapp.generated.resources.common_time_wait
import kitshn.composeapp.generated.resources.common_time_work
import kitshn.composeapp.generated.resources.common_unknown
import kitshn.composeapp.generated.resources.error_unallocated_ingredients
import kitshn.composeapp.generated.resources.recipe_not_found
import kitshn.composeapp.generated.resources.share_incentive
import kitshn.composeapp.generated.resources.share_title
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

val RecipeServingsAmountSaveMap = mutableMapOf<Int, Double>()

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ViewRecipeDetails(
    p: ViewParameters,

    client: TandoorClient?,
    recipeId: Int,
    shareToken: String? = null,

    navigationIcon: @Composable (() -> Unit)? = null,
    prependContent: @Composable () -> Unit = { },

    overridePaddingValues: PaddingValues? = null,
    hideFab: Boolean = false,

    onClickKeyword: (keyword: TandoorKeywordOverview) -> Unit = {},
    onServingsChange: (servings: Double) -> Unit = {},

    overrideServings: Double? = null
) {
    val context = LocalPlatformContext.current
    val imageLoader = remember { ImageLoader(context) }

    val websiteHandler = launchWebsiteHandler()
    val shareContentHandler = shareContentHandler()

    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

    // settings
    val hideIngredientAllocationActionChips =
        p.vm.settings.getHideIngredientAllocationActionChips.collectAsState(initial = false)
    val ingredientsShowFractionalValues =
        p.vm.settings.getIngredientsShowFractionalValues.collectAsState(initial = true)
    val propertiesShowFractionalValues =
        p.vm.settings.getPropertiesShowFractionalValues.collectAsState(initial = true)

    var pageLoadingState by rememberErrorLoadingSuccessState()

    val recipeIngredientAllocationDialogState =
        rememberRecipeIngredientAllocationDialogState(key = "ViewRecipeDetails/recipeIngredientAllocationDialogState")
    val recipeEditDialogState =
        rememberRecipeEditDialogState(key = "ViewRecipeDetails/recipeEditDialogState")
    val mealPlanCreationDialogState =
        rememberMealPlanCreationDialogState(key = "ViewRecipeDetails/mealPlanCreationDialogState")
    val manageRecipeInRecipeBooksDialogState = rememberManageRecipeInRecipeBooksDialogState()
    val recipeAddToShoppingDialogState = rememberRecipeAddToShoppingDialogState()

    val recipeAddToShoppingRequestState = rememberTandoorRequestState()

    val recipeDeleteDialogState = rememberCommonDeletionDialogState<TandoorRecipe>()

    val recipeActivitiesBottomSheetState = rememberRecipeActivitiesBottomSheetState()

    val launchTimerInfoBottomSheetState = rememberLaunchTimerInfoBottomSheetState()
    val launchTimerHandler = launchTimerHandler(p.vm, launchTimerInfoBottomSheetState)

    val recipeOverview = client?.container?.recipeOverview?.get(recipeId)
    if(recipeOverview == null) {
        FullSizeAlertPane(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = stringResource(Res.string.recipe_not_found),
            text = stringResource(Res.string.recipe_not_found)
        )

        return
    }

    LaunchedEffect(overrideServings) {
        if(overrideServings == null) return@LaunchedEffect
        RecipeServingsAmountSaveMap[recipeOverview.id] = overrideServings
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())
    val appBarTitleAlphaAnim = remember { Animatable(0f) }
    var titleCheckpointX by remember { mutableFloatStateOf(0f) }

    // calculate top bar title visibility
    LaunchedEffect(recipeId) {
        while(true) {
            delay(50)

            val offset = (-scrollBehavior.state.contentOffset) - 200f
            if(offset > titleCheckpointX) {
                if(appBarTitleAlphaAnim.value == 0f) appBarTitleAlphaAnim.animateTo(1f, tween(500))
            } else {
                if(appBarTitleAlphaAnim.value == 1f) appBarTitleAlphaAnim.animateTo(0f, tween(500))
            }
        }
    }

    val recipe = p.vm.tandoorClient!!.container.recipe.getOrElse(recipeOverview.id) { null }

    val fetchRequestState = rememberTandoorRequestState()
    LaunchedEffect(recipeId) {
        pageLoadingState = ErrorLoadingSuccessState.LOADING

        fetchRequestState.wrapRequest {
            p.vm.tandoorClient?.recipe?.get(
                id = recipeOverview.id,
                share = shareToken
            )
        }
    }

    val recipeLinkDialogState = rememberRecipeLinkDialogState()

    // calculate ingredients list
    val sortedStepsList = remember { mutableStateListOf<TandoorStep>() }
    val sortedIngredientsList = remember { mutableStateListOf<TandoorIngredient>() }
    val sortedAndMergedIngredientsList = remember { mutableStateListOf<TandoorIngredient>() }

    // sort steps and ingredients
    LaunchedEffect(recipe) {
        val beginLoading = Clock.System.now().toEpochMilliseconds()

        sortedIngredientsList.clear()
        sortedAndMergedIngredientsList.clear()
        if(recipe == null) return@LaunchedEffect

        sortedStepsList.clear()

        // adding delay to fix issue where markdown content wasn't updated accordingly when deleting steps
        delay(50)

        sortedStepsList.addAll(recipe.sortSteps())

        sortedStepsList.forEach {
            sortedIngredientsList.addAll(it.ingredients)

            // merge ingredients if they have equal food, unit and note
            it.ingredients.forEach { ingredient ->
                val duplicateIngredientIndex =
                    sortedAndMergedIngredientsList.indexOfFirst { duplIngredient ->
                        if(duplIngredient.is_header) return@indexOfFirst false
                        if(duplIngredient.no_amount) return@indexOfFirst false
                        if(ingredient.food?.id != duplIngredient.food?.id) return@indexOfFirst false
                        if(ingredient.unit?.id != duplIngredient.unit?.id) return@indexOfFirst false
                        if(ingredient.note != duplIngredient.note) return@indexOfFirst false
                        return@indexOfFirst true
                    }

                if(duplicateIngredientIndex == -1) {
                    sortedAndMergedIngredientsList.add(ingredient)
                } else {
                    val duplicateIngredient =
                        sortedAndMergedIngredientsList[duplicateIngredientIndex]

                    sortedAndMergedIngredientsList.removeAt(duplicateIngredientIndex)
                    sortedAndMergedIngredientsList.add(
                        duplicateIngredientIndex,
                        TandoorIngredient(
                            id = -1,
                            food = ingredient.food,
                            unit = ingredient.unit,
                            amount = ingredient.amount + duplicateIngredient.amount,
                            note = ingredient.note,
                            order = -1,
                            is_header = ingredient.is_header,
                            no_amount = ingredient.no_amount,
                            original_text = "-1",
                            always_use_plural_unit = ingredient.always_use_plural_unit,
                            always_use_plural_food = ingredient.always_use_plural_food
                        )
                    )
                }
            }

            sortedIngredientsList.addAll(it.ingredients)
        }

        val delay = 400 - (Clock.System.now().toEpochMilliseconds() - beginLoading)
        if(delay > 0L) delay(delay)

        pageLoadingState = ErrorLoadingSuccessState.SUCCESS
    }

    var servingsValue by remember { mutableDoubleStateOf(1.0) }
    var servingsFactor by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(recipe) {
        if(recipe == null) return@LaunchedEffect
        servingsValue = RecipeServingsAmountSaveMap[recipe.id] ?: recipe.servings.toDouble()
    }

    LaunchedEffect(servingsValue) {
        servingsFactor = servingsValue / (recipe?.servings ?: 1).toDouble()
        onServingsChange(servingsValue)

        if(recipe == null) return@LaunchedEffect
        RecipeServingsAmountSaveMap[recipe.id] = servingsValue
    }

    val useShareWrapperDialogState = rememberUseShareWrapperDialogState(vm = p.vm)

    val shareRequestState = rememberTandoorRequestState()
    fun share() = coroutineScope.launch {
        useShareWrapperDialogState.open { useShareWrapper ->
            coroutineScope.launch {
                shareRequestState.wrapRequest {
                    if(recipe == null) return@wrapRequest

                    val shareLink = recipe.retrieveShareLink() ?: ""
                    val link = if(useShareWrapper) {
                        BuildConfig.SHARE_WRAPPER_URL + if(shareLink.startsWith("https://")) {
                            shareLink.substring(8)
                        } else {
                            shareLink
                        }
                    } else {
                        shareLink
                    }

                    shareContentHandler(
                        getString(Res.string.share_title),
                        "${getString(Res.string.share_incentive)}\n \n» ${recipe.name} «\n$link"
                    )
                }

                hapticFeedback.handleTandoorRequestState(shareRequestState)
            }
        }
    }

    @Composable
    fun SourceButton() {
        var showSourceDialog by remember { mutableStateOf(false) }
        if(showSourceDialog) AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            icon = { Icon(Icons.Rounded.Link, stringResource(Res.string.common_source)) },
            title = { Text(text = stringResource(Res.string.common_source)) },
            text = { Text(text = recipe?.source_url ?: stringResource(Res.string.common_unknown)) },
            confirmButton = {
                Button(
                    onClick = { showSourceDialog = false }
                ) {
                    Text(text = stringResource(Res.string.common_okay))
                }
            }
        )

        Box(
            Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if((recipe?.source_url ?: "").isNotBlank()) OutlinedButton(
                onClick = {
                    try {
                        websiteHandler(recipe?.source_url!!)
                    } catch(e: Exception) {
                        showSourceDialog = true
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.OpenInNew,
                    stringResource(Res.string.action_open_source)
                )

                Spacer(Modifier.width(8.dp))

                Text(stringResource(Res.string.action_open_original))
            }
        }
    }

    val scrollState = rememberScrollState()
    var expandedToolbar by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    if(navigationIcon != null) {
                        navigationIcon()
                    } else {
                        BackButton(p.back, true)
                    }
                },
                title = {
                    Text(
                        text = recipeOverview.name,
                        Modifier.alpha(appBarTitleAlphaAnim.value),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if(hideFab) return@Scaffold

            HorizontalFloatingToolbar(
                expanded = expandedToolbar,
                content = {
                    RecipeFavoriteButton(
                        recipeOverview = recipeOverview,
                        favoritesViewModel = p.vm.favorites
                    )

                    IconButton(
                        onClick = { recipe?.let { recipeEditDialogState.open(it) } }
                    ) {
                        Icon(Icons.Rounded.Edit, stringResource(Res.string.action_edit))
                    }

                    IconButton(
                        onClick = { recipe?.let { recipeDeleteDialogState.open(it) } }
                    ) {
                        Icon(Icons.Rounded.Delete, stringResource(Res.string.action_delete))
                    }

                    IconButton(
                        onClick = { share() }
                    ) {
                        Icon(Icons.Rounded.Share, stringResource(Res.string.action_share))
                    }

                    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

                    Box {
                        IconButton(
                            onClick = { isMenuExpanded = true },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Icon(Icons.Rounded.MoreVert, stringResource(Res.string.action_more))
                        }

                        RecipeDetailsDropdown(
                            expanded = isMenuExpanded,
                            onManageRecipeBooks = {
                                manageRecipeInRecipeBooksDialogState.open(
                                    recipeId
                                )
                            },
                            onAddToMealPlan = {
                                coroutineScope.launch {
                                    val userPreference = client.userPreference.fetch()

                                    mealPlanCreationDialogState.open(
                                        MealPlanCreationAndEditDefaultValues(
                                            recipeId = recipeOverview.id,
                                            servings = recipeOverview.servings.toDouble(),
                                            shared = userPreference.plan_share
                                        )
                                    )
                                }
                            },
                            onAddToShopping = {
                                recipe?.let {
                                    recipeAddToShoppingDialogState.open(
                                        recipe = it,
                                        servings = servingsValue
                                    )
                                }
                            },
                            onAllocateIngredients = {
                                recipe?.let {
                                    recipeIngredientAllocationDialogState.open(
                                        it
                                    )
                                }
                            }
                        ) {
                            isMenuExpanded = false
                        }
                    }
                },
                floatingActionButton = {
                    FloatingToolbarDefaults.StandardFloatingActionButton(
                        onClick = {
                            p.vm.navHostController?.navigate("recipe/${recipeOverview.id}/cook/${servingsValue}")
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                        }
                    ) {
                        Icon(
                            Icons.Rounded.LocalDining,
                            stringResource(Res.string.action_start_cooking)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pvOg ->
        val pv = overridePaddingValues ?: pvOg

        Column(
            Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .floatingToolbarVerticalNestedScroll(
                    expanded = expandedToolbar,
                    onExpand = { expandedToolbar = true },
                    onCollapse = { expandedToolbar = false }
                )
                .padding(
                    bottom = pv.calculateBottomPadding()
                )
                .verticalScroll(scrollState)
        ) {
            var notEnoughSpace by remember { mutableStateOf(false) }

            Box {
                val roundness = getImageRoundness()

                AsyncImage(
                    model = recipeOverview.loadThumbnail(),
                    contentDescription = recipeOverview.name,
                    contentScale = ContentScale.Crop,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = roundness,
                                bottomEnd = roundness
                            )
                        )
                )
            }

            var disableSideBySideLayout by remember { mutableStateOf(false) }

            ResponsiveSideBySideLayout(
                rightMinWidth = 300.dp,
                rightMaxWidth = 500.dp,
                leftMinWidth = 300.dp,
                disable = disableSideBySideLayout,
                leftLayout = { enoughSpace ->
                    notEnoughSpace = !enoughSpace

                    Column(
                        Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 8.dp
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .onGloballyPositioned { lc ->
                                    titleCheckpointX = lc.boundsInRoot().bottom
                                }
                                .fillMaxWidth(),
                            text = recipeOverview.name,
                            style = Typography().titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }

                    LazyRow(
                        Modifier.padding(bottom = 16.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        items(
                            recipeOverview.keywords.size,
                            key = { recipeOverview.keywords[it].id })
                        { index ->
                            val keywordOverview = recipeOverview.keywords[index]
                            if(keywordOverview.label.startsWith(KITSHN_KEYWORD_FLAG_PREFIX)) return@items

                            FilterChip(onClick = {
                                onClickKeyword(keywordOverview)
                            }, label = {
                                Text(keywordOverview.label)
                            }, selected = true)
                        }
                    }

                    prependContent()

                    FlowRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val workingTime = recipe?.working_time ?: 1
                        if(workingTime > 0) RecipeInfoBlob(
                            icon = Icons.Rounded.Person,
                            label = stringResource(Res.string.common_time_work),
                            loadingState = pageLoadingState
                        ) {
                            Text(
                                text = workingTime.formatDuration()
                            )
                        }

                        if(recipe == null || recipe.rating != null) RecipeInfoBlob(
                            icon = Icons.Rounded.Reviews,
                            label = stringResource(Res.string.common_review),
                            loadingState = pageLoadingState
                        ) {
                            FiveStarIconRow(
                                rating = recipe?.rating ?: 5.0
                            )
                        }

                        val waitingTime = recipe?.waiting_time ?: 1
                        if(waitingTime > 0) RecipeInfoBlob(
                            icon = Icons.Rounded.Pause,
                            label = stringResource(Res.string.common_time_wait),
                            loadingState = pageLoadingState
                        ) {
                            Text(
                                text = waitingTime.formatDuration()
                            )
                        }
                    }

                    if(!recipeOverview.description.isNullOrBlank()) Text(
                        text = recipeOverview.description,
                        Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    )

                    RecipeActivityPreviewCard(
                        Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 8.dp
                            )
                            .fillMaxWidth(),
                        recipe = recipe
                    ) {
                        recipe?.let { recipeActivitiesBottomSheetState.open(it) }
                    }

                    if(enoughSpace) SourceButton()
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ServingsSelector(
                        value = servingsValue,
                        label = recipe?.servings_text ?: "",
                        loadingState = pageLoadingState
                    ) { value ->
                        servingsValue = value
                    }

                    if(recipe?.showIngredientAllocationActionChip() == true && !hideIngredientAllocationActionChips.value) {
                        val addFlagRequestState = rememberTandoorRequestState()

                        Row(
                            Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SmallFloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error,
                                elevation = FloatingActionButtonDefaults.elevation(
                                    0.dp,
                                    0.dp,
                                    0.dp,
                                    0.dp
                                ),
                                onClick = {
                                    coroutineScope.launch {
                                        addFlagRequestState.wrapRequest {
                                            // add HIDE_INGREDIENT_ALLOCATION_ACTION_CHIP flag to recipe

                                            recipe.addFlag(
                                                name = KITSHN_KEYWORD_FLAG__HIDE_INGREDIENT_ALLOCATION_ACTION_CHIP,
                                                description = KITSHN_KEYWORD_FLAG__HIDE_INGREDIENT_ALLOCATION_ACTION_CHIP_DESC
                                            )
                                        }

                                        hapticFeedback.handleTandoorRequestState(addFlagRequestState)
                                    }
                                }
                            ) {
                                IconWithState(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(Res.string.action_close),
                                    state = addFlagRequestState.state.toIconWithState()
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            WideActionChip(
                                modifier = Modifier.fillMaxWidth(),
                                type = WideActionChipType.INFO,
                                actionLabel = stringResource(Res.string.error_unallocated_ingredients)
                            ) {
                                recipeIngredientAllocationDialogState.open(recipe)
                            }
                        }
                    }

                    Box(
                        Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    ) {
                        IngredientsList(
                            list = sortedAndMergedIngredientsList,
                            factor = servingsFactor,
                            loadingState = pageLoadingState,
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            showFractionalValues = ingredientsShowFractionalValues.value,
                            onNotEnoughSpace = {
                                disableSideBySideLayout = true
                            }
                        )
                    }
                }
            }

            if (pageLoadingState == ErrorLoadingSuccessState.LOADING) {
                repeat(3) {
                    RecipeStepCard(
                        Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .fillMaxWidth(),
                        loadingState = pageLoadingState,
                        servingsFactor = servingsFactor,
                        showFractionalValues = ingredientsShowFractionalValues.value,
                        onClickRecipeLink = { },
                        onStartTimer = { seconds, timerName ->
                            launchTimerHandler(seconds, timerName)
                        }
                    )
                }
            } else {
                var index = 0
                sortedStepsList.forEach { step ->
                    RecipeStepCard(
                        Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .fillMaxWidth(),
                        recipe = recipe,
                        step = step,
                        stepIndex = index,
                        hideIngredients = step.ingredients.size == sortedIngredientsList.size,
                        servingsFactor = servingsFactor,
                        showFractionalValues = ingredientsShowFractionalValues.value,
                        onClickRecipeLink = { recipe ->
                            // show recipe link bottom sheet
                            recipeLinkDialogState.open(recipe.toOverview())
                        },
                        onStartTimer = { seconds, timerName ->
                            launchTimerHandler(seconds, timerName)
                        }
                    )

                    index++
                }
            }

            RecipePropertiesCard(
                Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth(),
                recipe = recipe,
                servingsFactor = servingsFactor,
                prependContent = {
                    HorizontalDivider(
                        Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    )
                },
                showFractionalValues = propertiesShowFractionalValues.value
            )

            if(notEnoughSpace && (recipe?.source_url ?: "").isNotBlank()) {
                SourceButton()
            } else {
                Spacer(Modifier.height(70.dp))
            }
        }

        StatusBarBackground()
    }

    RecipeIngredientAllocationDialog(
        state = recipeIngredientAllocationDialogState,
        showFractionalValues = ingredientsShowFractionalValues.value
    ) {
        // refresh recipe after ingredient allocation
        coroutineScope.launch {
            fetchRequestState.wrapRequest {
                client.recipe.get(recipeOverview.id).toOverview().let {
                    client.container.recipeOverview[it.id] = it
                }
            }
        }
    }

    // DIALOGS

    if(p.vm.tandoorClient != null) {
        RecipeCreationAndEditDialog(
            client = p.vm.tandoorClient!!,
            showFractionalValues = ingredientsShowFractionalValues.value,
            editState = recipeEditDialogState,
            onRefresh = {
                // refresh recipe after edit
                coroutineScope.launch {
                    fetchRequestState.wrapRequest {
                        client.recipe.get(recipeOverview.id).toOverview().let {
                            client.container.recipeOverview[it.id] = it
                        }
                    }
                }
            },
            onStartTimer = { seconds, timerName ->
                launchTimerHandler(seconds, timerName)
            }
        )

        CommonDeletionDialog(
            state = recipeDeleteDialogState,
            onConfirm = {
                coroutineScope.launch {
                    val request = TandoorRequestState().apply {
                        wrapRequest {
                            it.delete()
                            p.back?.let { it() }
                        }
                    }

                    hapticFeedback.handleTandoorRequestState(request)
                }
            }
        )

        MealPlanCreationAndEditDialog(
            client = p.vm.tandoorClient!!,
            creationState = mealPlanCreationDialogState,
            showFractionalValues = ingredientsShowFractionalValues.value
        ) {
            p.vm.mainSubNavHostController?.navigate("mealplan")
        }

        ManageRecipeInRecipeBooksDialog(
            client = p.vm.tandoorClient!!,
            favoritesRecipeBookId = p.vm.favorites.getFavoritesRecipeBookIdSync(),
            state = manageRecipeInRecipeBooksDialogState
        )

        RecipeAddToShoppingDialog(
            state = recipeAddToShoppingDialogState,
            showFractionalValues = ingredientsShowFractionalValues.value,
            onSubmit = { ingredients, servings ->
                coroutineScope.launch {
                    recipeAddToShoppingRequestState.wrapRequest {
                        recipe?.shopping(
                            ingredients = ingredients.map { ingredient -> ingredient.id },
                            servings = servings
                        )
                    }
                }
            }
        )

        RecipeActivitiesBottomSheet(
            client = client,
            state = recipeActivitiesBottomSheetState
        )
    }

    RecipeLinkDialog(
        p = p,
        state = recipeLinkDialogState
    )

    UseShareWrapperDialog(
        state = useShareWrapperDialogState
    )

    LaunchTimerInfoBottomSheet(
        vm = p.vm,
        state = launchTimerInfoBottomSheetState
    )

    TandoorRequestErrorHandler(fetchRequestState)
    TandoorRequestErrorHandler(shareRequestState)
    TandoorRequestErrorHandler(recipeAddToShoppingRequestState)
}

@Composable
expect fun StatusBarBackground()

@Composable
expect fun getImageRoundness(): Dp