#!/usr/bin/env python3
"""
Script para crear 1000 productos de prueba y probar la paginación
Autor: Generado para práctica de Spring Boot - Paginación
"""

import requests
import random
import time
from faker import Faker

# Configuración
BASE_URL = "http://localhost:8080/api"
fake = Faker('es_ES')  # Datos en español

# Colores para output en consola
class Colors:
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    END = '\033[0m'
    BOLD = '\033[1m'

def print_success(msg):
    print(f"{Colors.GREEN}[OK]{Colors.END} {msg}")

def print_info(msg):
    print(f"{Colors.BLUE}[INFO]{Colors.END} {msg}")

def print_warning(msg):
    print(f"{Colors.YELLOW}[WARN]{Colors.END} {msg}")

def print_error(msg):
    print(f"{Colors.RED}[ERROR]{Colors.END} {msg}")

def print_header(msg):
    print(f"\n{Colors.BOLD}{Colors.CYAN}{'='*60}{Colors.END}")
    print(f"{Colors.BOLD}{Colors.CYAN}{msg.center(60)}{Colors.END}")
    print(f"{Colors.BOLD}{Colors.CYAN}{'='*60}{Colors.END}\n")

# ============== LIMPIEZA DE DATOS ==============

def delete_all_products():
    """Elimina todos los productos existentes"""
    print_header("LIMPIANDO PRODUCTOS EXISTENTES")
    
    try:
        response = requests.get(f"{BASE_URL}/products/all")
        print_info(f"GET {BASE_URL}/products/all - Status: {response.status_code}")
        if response.status_code == 200:
            products = response.json()
            print_info(f"Respuesta: {type(products)} - Longitud: {len(products) if hasattr(products, '__len__') else 'N/A'}")
            if products:
                print_info(f"Encontrados {len(products)} productos para eliminar")
                deleted_count = 0
                
                for product in products:
                    try:
                        delete_response = requests.delete(f"{BASE_URL}/products/{product['id']}")
                        if delete_response.status_code == 204:
                            deleted_count += 1
                            if deleted_count % 100 == 0:
                                print_info(f"Eliminados {deleted_count}/{len(products)} productos...")
                    except Exception as e:
                        print_warning(f"Error eliminando producto ID {product['id']}: {str(e)}")
                
                print_success(f"Total productos eliminados: {deleted_count}/{len(products)}")
            else:
                print_info("No hay productos para eliminar")
        else:
            print_error(f"Error en GET /products/all: {response.status_code} - {response.text}")
    except Exception as e:
        print_error(f"Excepción al intentar eliminar productos: {str(e)}")

def delete_all_users():
    """Elimina todos los usuarios existentes"""
    print_header("LIMPIANDO USUARIOS EXISTENTES")
    
    try:
        response = requests.get(f"{BASE_URL}/users")
        if response.status_code == 200:
            users = response.json()
            if users:
                print_info(f"Encontrados {len(users)} usuarios para eliminar")
                deleted_count = 0
                
                for user in users:
                    try:
                        delete_response = requests.delete(f"{BASE_URL}/users/{user['id']}")
                        if delete_response.status_code == 204:
                            deleted_count += 1
                    except Exception as e:
                        print_warning(f"Error eliminando usuario ID {user['id']}: {str(e)}")
                
                print_success(f"Total usuarios eliminados: {deleted_count}/{len(users)}")
            else:
                print_info("No hay usuarios para eliminar")
    except Exception as e:
        print_error(f"Error al intentar eliminar usuarios: {str(e)}")

def delete_all_categories():
    """Elimina todas las categorías existentes"""
    print_header("LIMPIANDO CATEGORÍAS EXISTENTES")
    
    try:
        response = requests.get(f"{BASE_URL}/categories")
        if response.status_code == 200:
            categories = response.json()
            if categories:
                print_info(f"Encontradas {len(categories)} categorías para eliminar")
                deleted_count = 0
                
                for category in categories:
                    try:
                        delete_response = requests.delete(f"{BASE_URL}/categories/{category['id']}")
                        if delete_response.status_code == 204:
                            deleted_count += 1
                    except Exception as e:
                        print_warning(f"Error eliminando categoría ID {category['id']}: {str(e)}")
                
                print_success(f"Total categorías eliminadas: {deleted_count}/{len(categories)}")
            else:
                print_info("No hay categorías para eliminar")
    except Exception as e:
        print_error(f"Error al intentar eliminar categorías: {str(e)}")

def cleanup_database():
    """Limpia toda la base de datos en el orden correcto"""
    print_header("INICIANDO LIMPIEZA COMPLETA DE BASE DE DATOS")
    print_warning("¡ATENCIÓN! Se eliminarán TODOS los datos existentes")
    
    delete_all_products()
    delete_all_users()
    delete_all_categories()
    
    print_success("Limpieza de base de datos completada")
    time.sleep(1)

# ============== DATOS REALISTAS POR CATEGORÍA ==============

CATEGORY_PRODUCTS = {
    "Electrónicos": {
        "products": [
            ("Laptop", ["Gaming", "Ultrabook", "Workstation", "Chromebook", "2-en-1"], 500, 3000),
            ("Smartphone", ["Pro", "Plus", "Lite", "Max", "Ultra"], 200, 1500),
            ("Tablet", ["Pro", "Air", "Mini", "Kids", "Standard"], 150, 1200),
            ("Monitor", ["4K", "Gaming", "Ultra Wide", "Curved", "Professional"], 200, 1500),
            ("Auriculares", ["Bluetooth", "Gaming", "Studio", "Deportivos", "Noise Cancelling"], 30, 400),
            ("Teclado", ["Mecánico", "Gaming", "Inalámbrico", "Ergonómico", "RGB"], 20, 200),
            ("Mouse", ["Gaming", "Ergonómico", "Vertical", "Inalámbrico", "Bluetooth"], 15, 150),
            ("Cámara", ["DSLR", "Mirrorless", "Compacta", "Action", "Instantánea"], 300, 2500),
        ],
        "descriptions": [
            "Tecnología de última generación con procesador de alto rendimiento",
            "Diseño elegante y moderno con materiales premium de alta calidad",
            "Características avanzadas para profesionales y entusiastas",
            "Conectividad completa: WiFi 6, Bluetooth 5.0, USB-C",
            "Pantalla de alta resolución con colores vibrantes y precisos",
            "Batería de larga duración que aguanta todo el día",
            "Compatible con los principales sistemas operativos del mercado"
        ]
    },
    "Ropa y Accesorios": {
        "products": [
            ("Camiseta", ["Deportiva", "Casual", "Polo", "Estampada", "Lisa"], 10, 50),
            ("Pantalón", ["Jean", "Cargo", "Deportivo", "Chino", "Slim Fit"], 25, 120),
            ("Zapatos", ["Deportivos", "Casuales", "Formales", "Running", "Trekking"], 40, 200),
            ("Chaqueta", ["Impermeable", "Deportiva", "Casual", "Formal", "Acolchada"], 50, 250),
            ("Gorra", ["Deportiva", "Snapback", "Trucker", "Baseball", "Bucket"], 15, 45),
            ("Mochila", ["Escolar", "Deportiva", "Urbana", "Laptop", "Hiking"], 30, 150),
            ("Reloj", ["Deportivo", "Elegante", "Smart", "Casual", "Cronógrafo"], 50, 500),
        ],
        "descriptions": [
            "Confeccionado con materiales de primera calidad y costuras reforzadas",
            "Diseño moderno y versátil que se adapta a cualquier ocasión",
            "Tallas disponibles desde XS hasta XXL para todo tipo de cuerpos",
            "Tela transpirable y cómoda ideal para uso diario",
            "Resistente al agua y fácil de lavar en lavadora",
            "Estilo contemporáneo que nunca pasa de moda",
            "Acabados de alta calidad con atención al detalle"
        ]
    },
    "Hogar y Jardín": {
        "products": [
            ("Sofá", ["3 plazas", "2 plazas", "Esquinero", "Cama", "Reclinable"], 300, 2000),
            ("Mesa", ["Comedor", "Centro", "Lateral", "Escritorio", "Plegable"], 100, 800),
            ("Silla", ["Comedor", "Oficina", "Gaming", "Plegable", "Bar"], 50, 400),
            ("Lámpara", ["Pie", "Mesa", "Techo", "LED", "Inteligente"], 20, 200),
            ("Alfombra", ["Persa", "Moderna", "Shaggy", "Outdoor", "Infantil"], 40, 500),
            ("Cojín", ["Decorativo", "Ergonómico", "Outdoor", "Lumbar", "Memory Foam"], 10, 60),
            ("Planta", ["Interior", "Exterior", "Suculenta", "Aromática", "Cactus"], 15, 100),
        ],
        "descriptions": [
            "Fabricado con materiales duraderos y resistentes al uso diario",
            "Diseño ergonómico que proporciona máximo confort y soporte",
            "Fácil de ensamblar con instrucciones claras incluidas",
            "Acabado premium que combina con cualquier decoración",
            "Materiales ecológicos y sostenibles certificados",
            "Garantía extendida del fabricante incluida",
            "Diseño contemporáneo que transforma cualquier espacio"
        ]
    },
    "Deportes": {
        "products": [
            ("Balón", ["Fútbol", "Básquet", "Vóley", "Rugby", "Handball"], 15, 80),
            ("Raqueta", ["Tenis", "Bádminton", "Squash", "Ping Pong", "Pádel"], 30, 300),
            ("Pesas", ["Ajustables", "Hexagonales", "Cromadas", "Kettlebell", "Set"], 20, 200),
            ("Bicicleta", ["Montaña", "Ruta", "Urbana", "BMX", "Eléctrica"], 200, 2500),
            ("Cuerda", ["Saltar", "Escalada", "Batida", "CrossFit", "Battle Rope"], 10, 60),
            ("Esterilla", ["Yoga", "Pilates", "Gimnasia", "Camping", "Antideslizante"], 15, 80),
            ("Guantes", ["Boxeo", "Gym", "Ciclismo", "Running", "Escalada"], 15, 100),
        ],
        "descriptions": [
            "Equipo profesional certificado para competición y entrenamiento",
            "Material resistente que soporta uso intensivo y condiciones extremas",
            "Diseñado ergonómicamente para prevenir lesiones deportivas",
            "Tecnología avanzada para mejorar el rendimiento atlético",
            "Cumple con estándares internacionales de calidad deportiva",
            "Ideal tanto para principiantes como deportistas avanzados",
            "Incluye accesorios y bolsa de transporte de cortesía"
        ]
    },
    "Libros": {
        "products": [
            ("Novela", ["Ficción", "Romance", "Misterio", "Thriller", "Histórica"], 10, 30),
            ("Manual", ["Técnico", "Escolar", "Universitario", "Profesional", "Referencia"], 20, 100),
            ("Cómic", ["Marvel", "DC", "Manga", "Europeo", "Independiente"], 8, 25),
            ("Biografía", ["Histórica", "Contemporánea", "Autobiografía", "Memorias", "Inspiracional"], 15, 35),
            ("Libro Infantil", ["Cuentos", "Educativo", "Ilustrado", "Actividades", "Pop-up"], 10, 30),
        ],
        "descriptions": [
            "Edición de alta calidad con tapa dura y páginas de papel premium",
            "Historia cautivadora que atrapa desde la primera página",
            "Escrito por autor bestseller reconocido internacionalmente",
            "Incluye ilustraciones a color de alta calidad",
            "Traducción cuidada que respeta el espíritu original",
            "Perfecto para regalo o colección personal",
            "Contenido enriquecedor que inspira y educa"
        ]
    },
    "Juguetes": {
        "products": [
            ("Muñeca", ["Barbie", "Baby", "Princesa", "Fashion", "Interactiva"], 15, 80),
            ("Carro", ["Control Remoto", "Colección", "Racing", "Transformable", "Eléctrico"], 20, 150),
            ("Puzzle", ["3D", "1000 piezas", "Infantil", "Madera", "Educativo"], 10, 50),
            ("Peluche", ["Osito", "Dinosaurio", "Unicornio", "Perro", "Gigante"], 15, 80),
            ("Juego Mesa", ["Estrategia", "Familiar", "Cartas", "Educativo", "Construcción"], 20, 100),
            ("LEGO", ["City", "Star Wars", "Creator", "Technic", "Friends"], 30, 200),
        ],
        "descriptions": [
            "Juguete seguro certificado libre de sustancias tóxicas",
            "Estimula la creatividad e imaginación de los niños",
            "Materiales duraderos que resisten el juego intensivo",
            "Diseño atractivo con colores brillantes y llamativos",
            "Recomendado por educadores y especialistas en desarrollo infantil",
            "Incluye instrucciones claras y piezas de repuesto",
            "Perfecto para desarrollo de habilidades motoras y cognitivas"
        ]
    },
    "Alimentación": {
        "products": [
            ("Cereal", ["Integral", "Chocolate", "Frutas", "Fitness", "Infantil"], 3, 12),
            ("Snack", ["Papas", "Galletas", "Frutos Secos", "Barras", "Orgánico"], 2, 10),
            ("Café", ["Grano", "Molido", "Instantáneo", "Descafeinado", "Premium"], 5, 30),
            ("Té", ["Verde", "Negro", "Herbal", "Frutal", "Premium"], 4, 20),
            ("Pasta", ["Italiana", "Integral", "Sin Gluten", "Rellena", "Artesanal"], 2, 15),
            ("Aceite", ["Oliva", "Girasol", "Coco", "Aguacate", "Sésamo"], 5, 25),
        ],
        "descriptions": [
            "Producto 100% natural sin conservantes ni aditivos artificiales",
            "Rico en nutrientes esenciales y vitaminas para tu salud",
            "Certificado orgánico por entidades reguladoras internacionales",
            "Empaque hermético que preserva la frescura y sabor",
            "Ingredientes cuidadosamente seleccionados de origen sustentable",
            "Fecha de caducidad extendida garantizando calidad óptima",
            "Libre de transgénicos y elaborado con prácticas éticas"
        ]
    },
    "Belleza y Salud": {
        "products": [
            ("Crema", ["Facial", "Corporal", "Anti-edad", "Hidratante", "Solar"], 10, 80),
            ("Shampoo", ["Anticaspa", "Hidratante", "Volumen", "Orgánico", "Reparador"], 8, 30),
            ("Perfume", ["Hombre", "Mujer", "Unisex", "Eau de Toilette", "Eau de Parfum"], 30, 150),
            ("Maquillaje", ["Base", "Labial", "Sombras", "Delineador", "Set Completo"], 10, 100),
            ("Vitaminas", ["Multivitamínico", "Omega 3", "Vitamina C", "Calcio", "Complejo B"], 15, 50),
        ],
        "descriptions": [
            "Fórmula dermatológicamente probada para todo tipo de piel",
            "Ingredientes naturales de alta calidad sin parabenos",
            "Resultados visibles desde las primeras aplicaciones",
            "Hipoalergénico y testado clínicamente por expertos",
            "Fragancia suave y agradable que perdura todo el día",
            "No testado en animales - cruelty free certificado",
            "Presentación elegante ideal para regalo especial"
        ]
    },
    "Automóvil": {
        "products": [
            ("Llanta", ["All Season", "Performance", "SUV", "Camioneta", "Deportiva"], 50, 250),
            ("Aceite", ["Sintético", "Semi-sintético", "Mineral", "Diesel", "Alto Rendimiento"], 15, 60),
            ("Batería", ["12V", "AGM", "Gel", "Start-Stop", "Heavy Duty"], 60, 200),
            ("Filtro", ["Aceite", "Aire", "Combustible", "Cabina", "Set Completo"], 10, 50),
            ("Limpiador", ["Motor", "Interior", "Cristales", "Llantas", "Carrocería"], 8, 30),
        ],
        "descriptions": [
            "Compatible con la mayoría de modelos y marcas de vehículos",
            "Garantía del fabricante por defectos de manufactura",
            "Instalación sencilla con instrucciones detalladas incluidas",
            "Fabricado bajo estrictos estándares de control de calidad",
            "Mejora el rendimiento y eficiencia de tu vehículo",
            "Producto original certificado para máxima confiabilidad",
            "Vida útil extendida con mantenimiento adecuado"
        ]
    },
    "Mascotas": {
        "products": [
            ("Alimento", ["Perro", "Gato", "Cachorro", "Premium", "Orgánico"], 15, 80),
            ("Collar", ["Antipulgas", "Identificación", "LED", "Reflectivo", "Personalizado"], 10, 40),
            ("Juguete", ["Peluche", "Interactivo", "Masticable", "Cuerda", "Pelota"], 5, 30),
            ("Cama", ["Ortopédica", "Lavable", "Térmica", "Impermeable", "Cueva"], 30, 150),
            ("Correa", ["Retráctil", "Reflectiva", "Multiposición", "Manos Libres", "Arnés"], 10, 50),
        ],
        "descriptions": [
            "Producto veterinario aprobado seguro para tu mascota",
            "Material hipoalergénico y no tóxico para uso diario",
            "Fácil de limpiar y mantener en condiciones óptimas",
            "Diseñado pensando en la comodidad de tu mejor amigo",
            "Resistente y duradero para mascotas activas y juguetonas",
            "Recomendado por veterinarios profesionales certificados",
            "Disponible en múltiples tamaños para todas las razas"
        ]
    }
}

# ============== CREACIÓN DE DATOS ==============

def create_categories():
    """Crea categorías de productos con descripciones completas"""
    print_header("CREANDO CATEGORÍAS")
    
    categories_data = {
        "Electrónicos": "Dispositivos tecnológicos y gadgets electrónicos de última generación para hogar y oficina",
        "Ropa y Accesorios": "Prendas de vestir, calzado y complementos de moda para toda la familia",
        "Hogar y Jardín": "Artículos de decoración, muebles y accesorios para embellecer tu hogar y jardín",
        "Deportes": "Equipamiento deportivo y accesorios para fitness, entrenamiento y actividades al aire libre",
        "Libros": "Literatura, libros educativos, cómics y material de lectura para todas las edades",
        "Juguetes": "Juguetes didácticos, de entretenimiento y desarrollo para niños de todas las edades",
        "Alimentación": "Productos alimenticios, bebidas y comestibles de alta calidad y frescura",
        "Belleza y Salud": "Productos de cuidado personal, cosméticos y suplementos para tu bienestar",
        "Automóvil": "Repuestos, accesorios y productos de mantenimiento para tu vehículo",
        "Mascotas": "Alimentos, accesorios y productos de cuidado para tus mascotas queridas"
    }
    
    category_ids = {}
    
    for name, description in categories_data.items():
        cat = {"name": name, "description": description}
        try:
            response = requests.post(f"{BASE_URL}/categories", json=cat)
            if response.status_code in [200, 201]:
                created = response.json()
                category_ids[name] = created['id']
                print_success(f"Categoría creada: {name} (ID: {created['id']})")
            else:
                print_error(f"Error creando categoría {name}: {response.status_code}")
        except Exception as e:
            print_error(f"Excepción creando categoría {name}: {str(e)}")
    
    print_info(f"Total categorías creadas: {len(category_ids)}")
    return category_ids

def create_users(count=20):
    """Crea usuarios de prueba con datos completos y realistas"""
    print_header("CREANDO USUARIOS")
    
    user_ids = []
    
    for i in range(count):
        # Generar datos de usuario realistas
        first_name = fake.first_name()
        last_name = fake.last_name()
        full_name = f"{first_name} {last_name}"
        
        # Email basado en el nombre
        email_base = f"{first_name.lower()}.{last_name.lower()}"
        email = fake.unique.email().replace(fake.email().split('@')[0], email_base)
        
        user = {
            "name": full_name,
            "email": email,
            "password": "Password123!"
        }
        
        try:
            response = requests.post(f"{BASE_URL}/users", json=user)
            if response.status_code in [200, 201]:
                created = response.json()
                user_ids.append(created['id'])
                print_success(f"Usuario creado: {full_name} ({email}) - ID: {created['id']}")
            else:
                print_error(f"Error creando usuario {full_name}: {response.status_code}")
        except Exception as e:
            print_error(f"Excepción creando usuario: {str(e)}")
    
    print_info(f"Total usuarios creados: {len(user_ids)}")
    return user_ids

def create_products(count=1000, user_ids=[], category_ids={}):
    """Crea productos con datos realistas según su categoría"""
    print_header(f"CREANDO {count} PRODUCTOS REALISTAS")
    
    if not user_ids or not category_ids:
        print_error("Se requieren usuarios y categorías para crear productos")
        return []
    
    product_ids = []
    created_count = 0
    
    print_info(f"Generando {count} productos con datos coherentes...")
    start_time = time.time()
    
    # Usar un set para rastrear nombres únicos
    used_names = set()
    
    # Distribuir productos entre categorías
    category_names = list(CATEGORY_PRODUCTS.keys())
    products_per_category = count // len(category_names)
    
    for category_name in category_names:
        category_id = category_ids.get(category_name)
        if not category_id:
            continue
            
        category_data = CATEGORY_PRODUCTS[category_name]
        products_list = category_data["products"]
        descriptions_pool = category_data["descriptions"]
        
        # Crear productos para esta categoría
        for _ in range(products_per_category):
            # Seleccionar tipo de producto
            product_type, variants, min_price, max_price = random.choice(products_list)
            variant = random.choice(variants)
            
            # Generar nombre descriptivo único
            brand = fake.company().split()[0]  # Usar primera palabra como marca
            base_name = f"{product_type} {variant} {brand}"
            
            # Asegurar unicidad agregando sufijo si es necesario
            name = base_name
            counter = 1
            while name in used_names:
                name = f"{base_name} {counter}"
                counter += 1
            used_names.add(name)
            
            # Generar descripción realista
            description = f"{name}. {random.choice(descriptions_pool)}. "
            description += random.choice(descriptions_pool)
            
            # Precio apropiado al rango del producto
            price = round(random.uniform(min_price, max_price), 2)
            
            # Stock realista
            stock = random.randint(5, 200)
            
            # Usuario aleatorio
            user_id = random.choice(user_ids)
            
            # Seleccionar 1-2 categorías relacionadas
            num_categories = random.randint(1, min(2, len(category_ids)))
            # La categoría principal siempre incluida
            selected_category_ids = [category_id]
            
            # Agregar otra categoría relacionada ocasionalmente
            if num_categories > 1:
                other_cats = [cid for cn, cid in category_ids.items() if cn != category_name]
                if other_cats:
                    selected_category_ids.append(random.choice(other_cats))
            
            product = {
                "name": name,
                "description": description,
                "price": price,
                "stock": stock,
                "userId": user_id,
                "categoryIds": selected_category_ids
            }
            
            try:
                response = requests.post(f"{BASE_URL}/products", json=product)
                if response.status_code == 201:
                    created = response.json()
                    product_ids.append(created['id'])
                    created_count += 1
                    
                    # Mostrar progreso cada 100 productos
                    if created_count % 100 == 0:
                        elapsed = time.time() - start_time
                        rate = created_count / elapsed if elapsed > 0 else 0
                        print_info(f"Progreso: {created_count}/{count} productos ({rate:.1f}/seg)")
                else:
                    print_warning(f"Error creando producto: {response.status_code}")
            except Exception as e:
                print_warning(f"Excepción: {str(e)}")
    
    elapsed_time = time.time() - start_time
    print_success(f"Total productos creados: {created_count}/{count}")
    print_info(f"Tiempo total: {elapsed_time:.2f} segundos")
    if elapsed_time > 0:
        print_info(f"Velocidad promedio: {created_count/elapsed_time:.2f} productos/segundo")
    
    return product_ids

# ============== PRUEBAS DE PAGINACIÓN ==============

def test_pagination_basic():
    """Prueba paginación básica"""
    print_header("PRUEBA: PAGINACIÓN BÁSICA")
    
    tests = [
        {"page": 0, "size": 10, "desc": "Primera página, 10 elementos"},
        {"page": 0, "size": 5, "desc": "Primera página, 5 elementos"},
        {"page": 5, "size": 20, "desc": "Página 5, 20 elementos"},
        {"page": 0, "size": 50, "desc": "Primera página, 50 elementos"}
    ]
    
    for test in tests:
        url = f"{BASE_URL}/products?page={test['page']}&size={test['size']}"
        try:
            response = requests.get(url)
            if response.status_code == 200:
                data = response.json()
                print_success(f"{test['desc']}")
                print(f"   Total elementos: {data['totalElements']}")
                print(f"   Total páginas: {data['totalPages']}")
                print(f"   Elementos en esta página: {data['numberOfElements']}")
                print(f"   Primera página: {data['first']}, Última página: {data['last']}")
            else:
                print_error(f"Error {response.status_code}: {test['desc']}")
        except Exception as e:
            print_error(f"Excepción en {test['desc']}: {str(e)}")
    
def test_pagination_sorting():
    """Prueba ordenamiento en paginación"""
    print_header("PRUEBA: ORDENAMIENTO")
    
    sorts = [
        {"sort": "name,asc", "desc": "Ordenar por nombre ascendente"},
        {"sort": "price,desc", "desc": "Ordenar por precio descendente"},
        {"sort": "stock,asc", "desc": "Ordenar por stock ascendente"},
        {"sort": "createdAt,desc", "desc": "Ordenar por fecha de creación descendente"}
    ]
    
    for sort_test in sorts:
        url = f"{BASE_URL}/products?page=0&size=5&sort={sort_test['sort']}"
        try:
            response = requests.get(url)
            if response.status_code == 200:
                data = response.json()
                print_success(f"{sort_test['desc']}")
                for i, product in enumerate(data['content'][:3], 1):
                    print(f"   {i}. {product['name']} - ${product['price']} (Stock: {product['stock']})")
            else:
                print_error(f"Error {response.status_code}: {sort_test['desc']}")
        except Exception as e:
            print_error(f"Excepción: {str(e)}")

def test_pagination_filters():
    """Prueba filtros con paginación"""
    print_header("PRUEBA: FILTROS CON PAGINACIÓN")
    
    filters = [
        {"params": "minPrice=500&maxPrice=1000", "desc": "Productos entre $500 y $1000"},
        {"params": "name=laptop", "desc": "Productos que contienen 'laptop'"},
        {"params": "minPrice=100&page=0&size=10", "desc": "Productos mayores a $100"}
    ]
    
    for filter_test in filters:
        url = f"{BASE_URL}/products/search?{filter_test['params']}"
        try:
            response = requests.get(url)
            if response.status_code == 200:
                data = response.json()
                print_success(f"{filter_test['desc']}")
                print(f"   Total encontrados: {data['totalElements']}")
                print(f"   Mostrando: {data['numberOfElements']} de {data['size']}")
            else:
                print_error(f"Error {response.status_code}: {filter_test['desc']}")
        except Exception as e:
            print_error(f"Excepción: {str(e)}")

def test_pagination_slice():
    """Prueba Slice para mejor performance"""
    print_header("PRUEBA: SLICE (MEJOR PERFORMANCE)")
    
    url = f"{BASE_URL}/products/slice?page=0&size=10&sort=createdAt,desc"
    try:
        start_time = time.time()
        response = requests.get(url)
        elapsed = time.time() - start_time
        
        if response.status_code == 200:
            data = response.json()
            print_success(f"Slice obtenido en {elapsed*1000:.2f}ms")
            print(f"   Elementos: {data['numberOfElements']}")
            print(f"   Tiene siguiente: {data.get('hasNext', 'N/A')}")
            print(f"   Tiene anterior: {data.get('hasPrevious', 'N/A')}")
            print(f"   Es primera: {data['first']}")
            print(f"   Es última: {data['last']}")
            
            # Comparar con Page
            url_page = f"{BASE_URL}/products?page=0&size=10&sort=createdAt,desc"
            start_time = time.time()
            response_page = requests.get(url_page)
            elapsed_page = time.time() - start_time
            
            print_info(f"Comparación con Page: {elapsed_page*1000:.2f}ms")
            if elapsed > 0:
                print_info(f"Diferencia de tiempo: {abs(elapsed_page - elapsed)*1000:.2f}ms")
        else:
            print_error(f"Error {response.status_code}")
    except Exception as e:
        print_error(f"Excepción: {str(e)}")

def test_user_products():
    """Prueba productos de un usuario específico"""
    print_header("PRUEBA: PRODUCTOS POR USUARIO")
    
    try:
        response = requests.get(f"{BASE_URL}/users")
        if response.status_code == 200:
            users = response.json()
            if users:
                user_id = users[0]['id']
                user_name = users[0]['name']
                
                url = f"{BASE_URL}/products/user/{user_id}?page=0&size=5&sort=price,desc"
                response = requests.get(url)
                
                if response.status_code == 200:
                    data = response.json()
                    print_success(f"Productos de {user_name} (ID: {user_id})")
                    print(f"   Total productos: {data['totalElements']}")
                    print(f"   Mostrando: {data['numberOfElements']}")
                    
                    for i, product in enumerate(data['content'], 1):
                        print(f"   {i}. {product['name']} - ${product['price']}")
                else:
                    print_error(f"Error {response.status_code}")
            else:
                print_warning("No hay usuarios en la base de datos")
    except Exception as e:
        print_error(f"Excepción: {str(e)}")

# ============== MAIN ==============

def main():
    print_header("SCRIPT DE PRUEBA DE PAGINACIÓN - SPRING BOOT")
    print_info("Este script ELIMINARÁ todos los datos y creará datos REALISTAS de prueba")
    
    try:
        print_info("Verificando conexión con el servidor...")
        response = requests.get(f"{BASE_URL}/products")
        print_success("Conexión exitosa con el servidor")
    except Exception as e:
        print_error(f"No se puede conectar al servidor: {str(e)}")
        print_error("Asegúrate de que la aplicación Spring Boot esté ejecutándose en http://localhost:8080")
        return
    
    # Limpieza
    cleanup_database()
    
    # Creación de datos realistas
    category_ids = create_categories()
    if not category_ids:
        print_error("No se pudieron crear categorías. Abortando.")
        return
    
    user_ids = create_users(20)
    if not user_ids:
        print_error("No se pudieron crear usuarios. Abortando.")
        return
    
    product_ids = create_products(1000, user_ids, category_ids)
    if not product_ids:
        print_error("No se pudieron crear productos. Abortando.")
        return
    
    # Pruebas
    time.sleep(1)
    
    test_pagination_basic()
    test_pagination_sorting()
    test_pagination_filters()
    test_pagination_slice()
    test_user_products()
    
    print_header("PRUEBAS COMPLETADAS")
    print_success(f"Se crearon {len(product_ids)} productos REALISTAS exitosamente")
    print_success("Todas las pruebas de paginación fueron ejecutadas")
    print_info("\nPuedes probar manualmente con:")
    print("  - http://localhost:8080/api/products?page=0&size=10")
    print("  - http://localhost:8080/api/products/slice?page=0&size=10")
    print("  - http://localhost:8080/api/products/search?name=laptop&page=0&size=5")

if __name__ == "__main__":
    main()
