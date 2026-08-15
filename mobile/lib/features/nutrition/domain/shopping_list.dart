// =====================================================================
// MiCoach — Lista de compras (mirror de NutritionDtos en backend).
// =====================================================================
class ShoppingListItem {
  final int id;
  final int? ingredientId;
  final String? itemName;
  final double? amount;
  final String? unit;
  final String? category;
  final bool checked;

  const ShoppingListItem({
    required this.id,
    this.ingredientId,
    this.itemName,
    this.amount,
    this.unit,
    this.category,
    this.checked = false,
  });

  factory ShoppingListItem.fromJson(Map<String, dynamic> json) => ShoppingListItem(
        id: json['id'] as int,
        ingredientId: json['ingredientId'] as int?,
        itemName: json['itemName'] as String?,
        amount: (json['amount'] as num?)?.toDouble(),
        unit: json['unit'] as String?,
        category: json['category'] as String?,
        checked: json['checked'] as bool? ?? false,
      );
}

class ShoppingList {
  final int id;
  final String name;
  final DateTime? weekStart;
  final List<ShoppingListItem> items;

  const ShoppingList({required this.id, required this.name, this.weekStart, this.items = const []});

  factory ShoppingList.fromJson(Map<String, dynamic> json) => ShoppingList(
        id: json['id'] as int,
        name: json['name'] as String,
        weekStart: json['weekStart'] != null ? DateTime.parse(json['weekStart'] as String) : null,
        items: (json['items'] as List<dynamic>? ?? const [])
            .map((e) => ShoppingListItem.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
