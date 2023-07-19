package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.local.*
import com.benjtissot.sellingmugs.services.categoryCollection

import database
import org.litote.kmongo.*


class CategoryRepository {
    companion object {

        suspend fun getAll() : List<Category> {
            return categoryCollection.find().toList()
        }

        /**
         * @param category the [Category] to be inserted
         */
        suspend fun insertCategory(category: Category) {
            categoryCollection.insertOne(category)
        }

        suspend fun deleteCategory(id: String){
            categoryCollection.deleteOne(Category::id eq id) //type safe
        }

        /**
         * @param category the [Category] to be updated (inserted if not existent)
         */
        suspend fun updateCategory(category: Category) : Category {
            categoryCollection.updateOneById(category.id, category, upsert())
            return category
        }

        suspend fun getCategoryById(categoryId: String) : Category? {
            return categoryCollection.findOne(Category::id eq categoryId)
        }

        suspend fun getCategories(categoryIds: List<String>) : List<Category> {
            return categoryCollection.find(Category::id `in` categoryIds).toList()
        }

    }
}