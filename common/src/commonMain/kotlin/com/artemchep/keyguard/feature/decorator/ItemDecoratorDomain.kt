package com.artemchep.keyguard.feature.decorator

import com.artemchep.keyguard.common.model.DSecret
import com.artemchep.keyguard.feature.home.vault.model.VaultItem2
import com.artemchep.keyguard.feature.localization.TextHolder

class ItemDecoratorDomain<Generic, Value>(
    private val selector: (DSecret) -> String?,
    private val factory: (String, TextHolder) -> Generic,
) : ItemDecorator<Generic, Value> {
    /**
     * Last shown domain, used to not repeat the sections
     * if it stays the same.
     */
    private var lastDomain: String? = null

    override suspend fun getOrNull(item: Value): Generic? {
        val domain = when (item) {
            is VaultItem2.Item -> selector(item.source)
            else -> return null
        } ?: return null
        
        println("ItemDecoratorDomain: Processing item ${item.source.name} with domain '$domain'")
        
        if (domain == lastDomain) {
            println("ItemDecoratorDomain: Skipping duplicate domain '$domain'")
            return null
        }

        lastDomain = domain
        println("ItemDecoratorDomain: Creating decorator for domain '$domain'")
        return factory(
            "decorator.domain.$domain",
            TextHolder.Value(domain),
        )
    }
}