package me.steven.indrev.tools

import me.steven.indrev.registry.IRItemRegistry
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient

enum class IRToolMaterial(
    private val miningLevel: Int,
    private val itemDurability: Int,
    private val miningSpeed: Float,
    private val attackDamage: Float,
    private val enchantability: Int,
    private val repairIngredient: () -> Ingredient?
) : ToolMaterial {
    TIN(1, 200, 4.0f, 1.0f, 14, { Ingredient.ofItems(IRItemRegistry.TIN_INGOT()) }),
    COPPER(2, 300, 4.5f, 1.0f, 14, { Ingredient.ofItems(IRItemRegistry.COPPER_INGOT()) }),
    STEEL(3, 500, 4.5f, 2.0f, 14, { Ingredient.ofItems(IRItemRegistry.STEEL_INGOT()) }),
    LEAD(3, 500, 4.5f, 2.0f, 14, { Ingredient.ofItems(IRItemRegistry.STEEL_INGOT()) }),
    BRONZE(3, 500, 4.5f, 2.0f, 14, { Ingredient.ofItems(IRItemRegistry.STEEL_INGOT()) }),
    SILVER(3, 500, 4.5f, 2.0f, 14, { Ingredient.ofItems(IRItemRegistry.STEEL_INGOT()) });

    override fun getAttackDamage(): Float = attackDamage
    override fun getDurability(): Int = itemDurability
    override fun getEnchantability(): Int = enchantability
    override fun getMiningLevel(): Int = miningLevel
    override fun getMiningSpeedMultiplier(): Float = miningSpeed
    override fun getRepairIngredient(): Ingredient? = repairIngredient()
}