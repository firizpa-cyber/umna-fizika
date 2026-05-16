package uz.fizika.formulas.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.fizika.core.database.dao.FormulaDao
import uz.fizika.core.database.dao.TopicDao
import uz.fizika.core.database.dao.UserProgressDao
import uz.fizika.core.database.entities.FormulaEntity
import uz.fizika.core.database.entities.TopicEntity
import uz.fizika.formulas.model.FormulaWithProgress
import uz.fizika.formulas.model.GraphNode
import uz.fizika.formulas.model.GraphEdge
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormulaRepository @Inject constructor(
    private val formulaDao: FormulaDao,
    private val topicDao: TopicDao,
    private val progressDao: UserProgressDao
) {
    fun getAllTopics(): Flow<List<TopicEntity>> = topicDao.getAllTopics()

    fun getFormulasByTopic(topicId: String): Flow<List<FormulaEntity>> =
        formulaDao.getFormulasByTopic(topicId)

    fun searchFormulas(query: String): Flow<List<FormulaEntity>> =
        formulaDao.searchFormulas(query)

    fun getAllFormulas(): Flow<List<FormulaEntity>> = formulaDao.getAllFormulas()

    suspend fun getFormulaById(id: String): FormulaEntity? =
        formulaDao.getFormulaById(id)

    suspend fun getFormulaGraph(formulaId: String): Pair<List<GraphNode>, List<GraphEdge>> {
        val links = formulaDao.getLinkedFormulas(formulaId)
        val nodeIds = (links.map { it.fromId } + links.map { it.toId }).distinct()
        val nodes = nodeIds.mapNotNull { id ->
            formulaDao.getFormulaById(id)?.let { f ->
                GraphNode(id = f.id, label = f.title, latex = f.latex, topicId = f.topicId)
            }
        }
        val edges = links.map { GraphEdge(it.fromId, it.toId, it.linkType) }
        return nodes to edges
    }

    suspend fun markViewed(formulaId: String) =
        progressDao.incrementView(formulaId)
}
