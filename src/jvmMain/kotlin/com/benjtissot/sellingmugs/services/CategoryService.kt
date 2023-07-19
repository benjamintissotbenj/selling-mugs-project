package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.local.*
import com.benjtissot.sellingmugs.getUuidFromString
import com.benjtissot.sellingmugs.repositories.CategoryRepository
import com.benjtissot.sellingmugs.repositories.MugRepository
import database
import org.litote.kmongo.*

val categoryCollection = database.getCollection<Category>()

class CategoryService {
    companion object {

        fun createCategory(name: String) : Category {
            if (name == "default") return Category("0", name)
            return Category(getUuidFromString(name), name)
        }

        suspend fun insertNewCategory(category: Category){
            CategoryRepository.updateCategory(category)
        }

        suspend fun deleteCategory(id: String){
            CategoryRepository.deleteCategory(id)
        }

        suspend fun updateCategory(category: Category) : Category {
            
            return CategoryRepository.updateCategory(category)
        }

        suspend fun getCategoryById(categoryId: String) : Category? {
            return CategoryRepository.getCategoryById(categoryId)
        }

        suspend fun getCategoryByName(name: String) : Category? {
            return CategoryRepository.getCategoryById(getUuidFromString(name))
        }

        suspend fun getAllCategories() : List<Category> {
            return CategoryRepository.getAll()
        }

        suspend fun getCategories(categoryIds: List<String>) : List<Category> {
            return CategoryRepository.getCategories(categoryIds)
        }

    }
}