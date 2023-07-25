package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.OPEN_AI_PATH
import com.benjtissot.sellingmugs.entities.local.Artwork
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
        route(Artwork.path){
            post {
                val params: MugsChatRequestParams = call.receive()
                try {
                    ImageGeneratorService.generateDesignFromParams(params)?.let {
                        call.respond(it)
                    } ?: run {
                        // if we ran 5 times, and it didn't manage to upload successfully
                        call.respond(Const.HttpStatusCode_ImageUploadFail)
                    }
                } catch (e: OpenAIUnavailable) {
                    e.printStackTrace()
                    call.respond(Const.HttpStatusCode_OpenAIUnavailable)
                }catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

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
                val chatRequestParams : CategoriesChatRequestParams? = try {
                    call.receive()
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest)
                    null
                }
                if (chatRequestParams != null) {
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
}