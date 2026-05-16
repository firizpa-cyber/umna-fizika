package uz.fizika.formulas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.fizika.core.database.entities.FormulaEntity
import uz.fizika.core.database.entities.TopicEntity
import uz.fizika.formulas.model.GraphEdge
import uz.fizika.formulas.model.GraphNode
import uz.fizika.formulas.repository.FormulaRepository
import javax.inject.Inject

data class FormulaListUiState(
    val topics: List<TopicEntity> = emptyList(),
    val formulas: List<FormulaEntity> = emptyList(),
    val selectedTopicId: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

data class FormulaDetailUiState(
    val formula: FormulaEntity? = null,
    val graphNodes: List<GraphNode> = emptyList(),
    val graphEdges: List<GraphEdge> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FormulaListViewModel @Inject constructor(
    private val repo: FormulaRepository
) : ViewModel() {

    private val _selectedTopicId = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<FormulaListUiState> = combine(
        repo.getAllTopics(),
        _selectedTopicId,
        _searchQuery.debounce(300)
    ) { topics, topicId, query -> Triple(topics, topicId, query) }
        .flatMapLatest { (topics, topicId, query) ->
            val formulaFlow = when {
                query.isNotBlank()  -> repo.searchFormulas(query)
                topicId != null     -> repo.getFormulasByTopic(topicId)
                else                -> repo.getAllFormulas()
            }
            formulaFlow.map { formulas ->
                FormulaListUiState(
                    topics         = topics,
                    formulas       = formulas,
                    selectedTopicId= topicId,
                    searchQuery    = query,
                    isLoading      = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FormulaListUiState())

    fun selectTopic(topicId: String?) { _selectedTopicId.value = topicId }
    fun onSearchQuery(query: String) { _searchQuery.value = query }
}

@HiltViewModel
class FormulaDetailViewModel @Inject constructor(
    private val repo: FormulaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormulaDetailUiState())
    val uiState: StateFlow<FormulaDetailUiState> = _uiState.asStateFlow()

    fun load(formulaId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val formula = repo.getFormulaById(formulaId)
        val (nodes, edges) = repo.getFormulaGraph(formulaId)
        _uiState.update {
            it.copy(formula = formula, graphNodes = nodes, graphEdges = edges, isLoading = false)
        }
        repo.markViewed(formulaId)
    }
}
