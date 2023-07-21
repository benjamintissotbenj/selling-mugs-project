package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.OPEN_AI_PATH
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.openAI.CategoriesChatRequestParams
import com.benjtissot.sellingmugs.entities.openAI.MugsChatRequestParams
import com.benjtissot.sellingmugs.entities.openAI.OpenAIUnavailable
import com.benjtissot.sellingmugs.repositories.CategoriesGenerationResultRepository
import com.benjtissot.sellingmugs.services.ImageGeneratorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*

private val LOG = KtorSimpleLogger("OpenAIController.kt")

fun Route.openAIRouting(){

    route(OPEN_AI_PATH) {
        route(Mug.path){
            post {
                val params: MugsChatRequestParams = call.receive()
                try {
                    call.respond(ImageGeneratorService.generateMugsFromParams(params))
                } catch (e: OpenAIUnavailable) {
                    e.printStackTrace()
                    call.respond(Const.HttpStatusCode_OpenAIUnavailable)
                }catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        route(Category.path){
            post {
                val chatRequestParams : CategoriesChatRequestParams = call.receive()
                try {
                    call.respond(
                        CategoriesGenerationResultRepository.updateGenerateCategoriesStatus (
                            ImageGeneratorService.generateCategoriesAndMugs(chatRequestParams)
                        )
                    )
                } catch (e: OpenAIUnavailable) {
                    e.printStackTrace()
                    call.respond(Const.HttpStatusCode_OpenAIUnavailable)
                }catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}