-- =====================================================================
-- KineticOs — V3: Schema nutrition
-- Módulo: nutrition (ingredientes, recetas, planes, sustituciones, compra)
-- Documentación: docs/02-database.md
-- =====================================================================

-- Catálogo de alimentos con macros por 100 g.
CREATE TABLE nutrition_ingredients (
    id                BIGSERIAL    PRIMARY KEY,
    name              VARCHAR(200) NOT NULL,
    category          VARCHAR(50)
        CHECK (category IN ('proteins', 'carbs', 'fats', 'vegetables', 'fruits',
                            'dairy', 'grains', 'legumes', 'nuts', 'condiments',
                            'beverages', 'other')),
    base_unit         VARCHAR(20)  NOT NULL DEFAULT 'g',
    calories_per_100g NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (calories_per_100g >= 0),
    protein_per_100g  NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (protein_per_100g >= 0),
    carbs_per_100g    NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (carbs_per_100g >= 0),
    fat_per_100g      NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (fat_per_100g >= 0),
    fiber_per_100g    NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (fiber_per_100g >= 0),
    is_ai_generated   BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_nutrition_ingredient_name UNIQUE (name)
);
CREATE INDEX idx_nutrition_ingredients_category ON nutrition_ingredients (category);

-- Recetas con macros por porción.
CREATE TABLE nutrition_recipes (
    id                  BIGSERIAL    PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    description         VARCHAR(1000),
    meal_category       VARCHAR(20)  NOT NULL
        CHECK (meal_category IN ('breakfast', 'lunch', 'dinner', 'snack', 'dessert', 'drink')),
    difficulty          VARCHAR(15)
        CHECK (difficulty IN ('easy', 'medium', 'hard')),
    servings            SMALLINT     NOT NULL DEFAULT 1 CHECK (servings >= 1),
    prep_time_min       SMALLINT,
    cook_time_min       SMALLINT,
    calories_per_serving NUMERIC(8,2),
    protein_per_serving NUMERIC(8,2),
    carbs_per_serving   NUMERIC(8,2),
    fat_per_serving     NUMERIC(8,2),
    fiber_per_serving   NUMERIC(8,2),
    instructions        TEXT,
    image_url           VARCHAR(500),
    is_ai_generated     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_nutrition_recipes_category ON nutrition_recipes (meal_category);

-- Ingredientes que componen una receta.
CREATE TABLE nutrition_recipe_ingredients (
    recipe_id     BIGINT         NOT NULL REFERENCES nutrition_recipes (id) ON DELETE CASCADE,
    ingredient_id BIGINT         NOT NULL REFERENCES nutrition_ingredients (id) ON DELETE RESTRICT,
    amount        NUMERIC(10,2)  NOT NULL CHECK (amount > 0),
    unit          VARCHAR(20)    NOT NULL DEFAULT 'g'
        CHECK (unit IN ('g', 'ml', 'unit', 'tbsp', 'tsp', 'cup', 'slice')),
    order_index   SMALLINT       NOT NULL DEFAULT 1,
    PRIMARY KEY (recipe_id, ingredient_id)
);

-- Planes de alimentación por usuario (con objetivo de macros).
CREATE TABLE nutrition_meal_plans (
    id                BIGSERIAL    PRIMARY KEY,
    user_id           BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    name              VARCHAR(200) NOT NULL,
    description       VARCHAR(1000),
    start_date        DATE         NOT NULL,
    end_date          DATE         NOT NULL,
    target_calories   INTEGER,
    target_protein_g  NUMERIC(8,2),
    target_carbs_g    NUMERIC(8,2),
    target_fat_g      NUMERIC(8,2),
    is_ai_generated   BOOLEAN      NOT NULL DEFAULT FALSE,
    status            VARCHAR(20)  NOT NULL DEFAULT 'active'
        CHECK (status IN ('draft', 'active', 'completed', 'cancelled')),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_nutrition_meal_plans_user ON nutrition_meal_plans (user_id);
CREATE INDEX idx_nutrition_meal_plans_dates ON nutrition_meal_plans (start_date, end_date);

-- Días de un plan (un registro por día calendario).
CREATE TABLE nutrition_meal_plan_days (
    id           BIGSERIAL PRIMARY KEY,
    meal_plan_id BIGINT    NOT NULL REFERENCES nutrition_meal_plans (id) ON DELETE CASCADE,
    plan_date    DATE      NOT NULL,
    CONSTRAINT uq_nutrition_meal_plan_day UNIQUE (meal_plan_id, plan_date)
);
CREATE INDEX idx_nutrition_meal_plan_days_date ON nutrition_meal_plan_days (plan_date);

-- Comidas dentro de un día del plan.
CREATE TABLE nutrition_meal_plan_meals (
    id               BIGSERIAL    PRIMARY KEY,
    meal_plan_day_id BIGINT       NOT NULL REFERENCES nutrition_meal_plan_days (id) ON DELETE CASCADE,
    recipe_id        BIGINT       REFERENCES nutrition_recipes (id) ON DELETE SET NULL,
    meal_type        VARCHAR(20)  NOT NULL
        CHECK (meal_type IN ('breakfast', 'lunch', 'dinner', 'snack')),
    order_index      SMALLINT     NOT NULL DEFAULT 1,
    servings         NUMERIC(5,2) NOT NULL DEFAULT 1,
    notes            VARCHAR(500)
);
CREATE INDEX idx_nutrition_meal_plan_meals_day ON nutrition_meal_plan_meals (meal_plan_day_id);

-- Sustituciones inteligentes de ingredientes.
CREATE TABLE nutrition_substitutions (
    id                      BIGSERIAL    PRIMARY KEY,
    recipe_id               BIGINT       REFERENCES nutrition_recipes (id) ON DELETE SET NULL,
    ingredient_id           BIGINT       NOT NULL REFERENCES nutrition_ingredients (id) ON DELETE RESTRICT,
    substitute_ingredient_id BIGINT      NOT NULL REFERENCES nutrition_ingredients (id) ON DELETE RESTRICT,
    reason                  VARCHAR(30)
        CHECK (reason IN ('allergy', 'intolerance', 'unavailable', 'preference')),
    notes                   VARCHAR(500),
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_nutrition_substitution_not_self CHECK (ingredient_id <> substitute_ingredient_id)
);
CREATE INDEX idx_nutrition_substitutions_ingredient ON nutrition_substitutions (ingredient_id);

-- Diario alimentario: qué comió el usuario y cuándo.
CREATE TABLE nutrition_daily_intake (
    id               BIGSERIAL    PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    meal_plan_meal_id BIGINT      REFERENCES nutrition_meal_plan_meals (id) ON DELETE SET NULL,
    recipe_id        BIGINT       REFERENCES nutrition_recipes (id) ON DELETE SET NULL,
    food_date        DATE         NOT NULL,
    meal_type        VARCHAR(20)  NOT NULL
        CHECK (meal_type IN ('breakfast', 'lunch', 'dinner', 'snack')),
    amount           NUMERIC(8,2),
    calories         NUMERIC(8,2),
    protein_g        NUMERIC(8,2),
    carbs_g          NUMERIC(8,2),
    fat_g            NUMERIC(8,2),
    consumed_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_nutrition_daily_intake_user ON nutrition_daily_intake (user_id, food_date);

-- Listas de compra por usuario.
CREATE TABLE nutrition_shopping_lists (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    name       VARCHAR(200) NOT NULL DEFAULT 'Lista de la semana',
    week_start DATE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_nutrition_shopping_lists_user ON nutrition_shopping_lists (user_id);

-- Ítems de la lista de compra.
CREATE TABLE nutrition_shopping_list_items (
    id               BIGSERIAL    PRIMARY KEY,
    shopping_list_id BIGINT       NOT NULL REFERENCES nutrition_shopping_lists (id) ON DELETE CASCADE,
    ingredient_id    BIGINT       REFERENCES nutrition_ingredients (id) ON DELETE SET NULL,
    item_name        VARCHAR(200),
    amount           NUMERIC(10,2),
    unit             VARCHAR(20),
    category         VARCHAR(50),
    is_checked       BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_nutrition_shopping_items_list ON nutrition_shopping_list_items (shopping_list_id);