package com.artemchep.keyguard.feature.home.vault.search.sort

import com.artemchep.keyguard.common.model.DSecret
import com.artemchep.keyguard.feature.home.vault.model.VaultItem2
import com.artemchep.keyguard.platform.parcelize.LeIgnoredOnParcel
import com.artemchep.keyguard.platform.parcelize.LeParcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@LeParcelize
@Serializable
object DomainSort : Sort, PureSort {
    @LeIgnoredOnParcel
    @Transient
    override val id: String = "domain"

    override fun compare(
        a: VaultItem2.Item,
        b: VaultItem2.Item,
    ): Int = kotlin.run {
        val aDomain = extractDomain(a.source)
        val bDomain = extractDomain(b.source)
        aDomain.compareTo(bDomain)
    }

    private fun extractDomain(secret: DSecret): String {
        // Only extract domain from URIs
        secret.uris.firstOrNull { uri ->
            uri.uri.startsWith("http://", ignoreCase = true) ||
                    uri.uri.startsWith("https://", ignoreCase = true)
        }?.let { uri ->
            return@extractDomain try {
                // Extract domain from URL
                val url = java.net.URL(uri.uri)
                url.host.removePrefix("www.")
            } catch (e: Exception) {
                println("DomainSort: Failed to parse URL ${uri.uri}: ${e.message}")
                uri.uri
            }
        }

        // Special section for entries without URIs (will sort to top)
        println("DomainSort: No valid URI found for secret ${secret.name}, using fallback")
        return "[No Domain]"
    }
}