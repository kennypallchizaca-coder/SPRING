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

# ============== CREACIÓN DE DATOS ==============

def create_categories():
    """Crea categorías de productos"""
    print_header("CREANDO/OBTENIENDO CATEGORÍAS")
    
    # Primero intentar obtener categorías existentes
    try:
        response = requests.get(f"{BASE_URL}/categories")
        if response.status_code == 200:
            existing_categories = response.json()
            if existing_categories:
                category_ids = [cat['id'] for cat in existing_categories]
                print_success(f"Categorías existentes encontradas: {len(category_ids)}")
                for cat in existing_categories:
                    print(f"   - {cat.get('nombre', cat.get('name', 'Sin nombre'))} (ID: {cat['id']})")
                return category_ids
    except Exception as e:
        print_warning(f"No se pudieron obtener categorías existentes: {str(e)}")
    
    categories = [
        {"name": "Electrónicos", "description": "Dispositivos y gadgets electrónicos"},
        {"name": "Ropa y Accesorios", "description": "Prendas de vestir y complementos"},
        {"name": "Hogar y Jardín", "description": "Artículos para el hogar y jardín"},
        {"name": "Deportes", "description": "Equipamiento deportivo y fitness"},
        {"name": "Libros", "description": "Libros físicos y digitales"},
        {"name": "Juguetes", "description": "Juguetes y juegos para niños"},
        {"name": "Alimentación", "description": "Productos alimenticios"},
        {"name": "Belleza y Salud", "description": "Productos de belleza y cuidado personal"},
        {"name": "Automóvil", "description": "Accesorios y repuestos para vehículos"},
        {"name": "Mascotas", "description": "Productos para mascotas"}
    ]
    
    category_ids = []
    
    for cat in categories:
        try:
            response = requests.post(f"{BASE_URL}/categories", json=cat)
            if response.status_code in [200, 201]:
                created = response.json()
                category_ids.append(created['id'])
                print_success(f"Categoría creada: {cat['name']} (ID: {created['id']})")
            else:
                print_error(f"Error creando categoría {cat['name']}: {response.status_code}")
        except Exception as e:
            print_error(f"Excepción creando categoría {cat['name']}: {str(e)}")
    
    print_info(f"Total categorías disponibles: {len(category_ids)}")
    return category_ids

def create_users(count=20):
    """Crea usuarios de prueba"""
    print_header("CREANDO/OBTENIENDO USUARIOS")
    
    user_ids = []
    
    # Primero intentar obtener usuarios existentes
    try:
        response = requests.get(f"{BASE_URL}/users")
        if response.status_code == 200:
            existing_users = response.json()
            if existing_users:
                user_ids = [user['id'] for user in existing_users]
                print_success(f"Usuarios existentes encontrados: {len(user_ids)}")
                for user in existing_users[:5]:
                    print(f"   - {user['name']} (ID: {user['id']})")
                if len(existing_users) > 5:
                    print(f"   ... y {len(existing_users) - 5} más")
                return user_ids
    except Exception as e:
        print_warning(f"No se pudieron obtener usuarios existentes: {str(e)}")
    
    # Si no hay usuarios, crear nuevos
    for i in range(count):
        user = {
            "name": fake.name(),
            "email": fake.unique.email(),
            "password": "Password123!"
        }
        
        try:
            response = requests.post(f"{BASE_URL}/users", json=user)
            if response.status_code in [200, 201]:
                created = response.json()
                user_ids.append(created['id'])
                print_success(f"Usuario creado: {user['name']} ({user['email']}) - ID: {created['id']}")
            else:
                print_error(f"Error creando usuario {user['name']}: {response.status_code}")
        except Exception as e:
            print_error(f"Excepción creando usuario: {str(e)}")
    
    print_info(f"Total usuarios disponibles: {len(user_ids)}")
    return user_ids

def create_products(count=1000, user_ids=[], category_ids=[]):
    """Crea productos de prueba"""
    print_header(f"CREANDO {count} PRODUCTOS")
    
    if not user_ids or not category_ids:
        print_error("Se requieren usuarios y categorías para crear productos")
        return []
    
    # Nombres de productos variados
    product_prefixes = [
        "Laptop", "Mouse", "Teclado", "Monitor", "Smartphone", "Tablet", "Auriculares",
        "Camiseta", "Pantalón", "Zapatos", "Chaqueta", "Gorra", "Mochila",
        "Sofá", "Mesa", "Silla", "Lámpara", "Cojín", "Alfombra",
        "Balón", "Raqueta", "Pesas", "Bicicleta", "Cuerda",
        "Novela", "Manual", "Enciclopedia", "Cómic", "Revista",
        "Muñeca", "Carro", "Puzzle", "Peluche", "Juego de mesa",
        "Cereal", "Snack", "Bebida", "Conserva", "Dulce",
        "Crema", "Shampoo", "Perfume", "Maquillaje", "Jabón",
        "Llanta", "Aceite", "Batería", "Filtro", "Limpiador",
        "Collar", "Correa", "Alimento", "Juguete", "Cama"
    ]
    
    product_suffixes = [
        "Pro", "Plus", "Ultra", "Premium", "Basic", "Deluxe", "Sport",
        "Classic", "Modern", "Vintage", "Eco", "Smart", "Digital"
    ]
    
    product_ids = []
    created_count = 0
    
    print_info(f"Iniciando creación de {count} productos...")
    start_time = time.time()
    
    for i in range(count):
        # Generar nombre de producto
        prefix = random.choice(product_prefixes)
        suffix = random.choice(product_suffixes)
        name = f"{prefix} {suffix} {fake.word().capitalize()}"
        
        # Generar precio aleatorio
        price = round(random.uniform(10, 5000), 2)
        
        # Stock aleatorio
        stock = random.randint(0, 500)
        
        # Usuario aleatorio
        user_id = random.choice(user_ids)
        
        # 1-3 categorías aleatorias
        num_categories = random.randint(1, min(3, len(category_ids)))
        selected_categories = random.sample(category_ids, num_categories)
        
        product = {
            "name": name,
            "description": fake.text(max_nb_chars=200),
            "price": price,
            "stock": stock,
            "userId": user_id,
            "categoryIds": selected_categories
        }
        
        try:
            response = requests.post(f"{BASE_URL}/products", json=product)
            if response.status_code == 201:
                created = response.json()
                product_ids.append(created['id'])
                created_count += 1
                
                # Mostrar progreso cada 100 productos
                if (i + 1) % 100 == 0:
                    elapsed = time.time() - start_time
                    rate = created_count / elapsed
                    print_info(f"Progreso: {i + 1}/{count} productos ({rate:.1f} productos/segundo)")
            else:
                print_warning(f"Error en producto {i + 1}: {response.status_code}")
        except Exception as e:
            print_warning(f"Excepción en producto {i + 1}: {str(e)}")
    
    elapsed_time = time.time() - start_time
    print_success(f"Total productos creados: {created_count}/{count}")
    print_info(f"Tiempo total: {elapsed_time:.2f} segundos")
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
            print(f"   Tiene siguiente: {data['hasNext']}")
            print(f"   Tiene anterior: {data['hasPrevious']}")
            print(f"   Es primera: {data['first']}")
            print(f"   Es última: {data['last']}")
            
            # Comparar con Page
            url_page = f"{BASE_URL}/products?page=0&size=10&sort=createdAt,desc"
            start_time = time.time()
            response_page = requests.get(url_page)
            elapsed_page = time.time() - start_time
            
            print_info(f"Comparación con Page: {elapsed_page*1000:.2f}ms")
            print_info(f"Slice es {(elapsed_page/elapsed):.2f}x más rápido")
        else:
            print_error(f"Error {response.status_code}")
    except Exception as e:
        print_error(f"Excepción: {str(e)}")

def test_user_products():
    """Prueba productos de un usuario específico"""
    print_header("PRUEBA: PRODUCTOS POR USUARIO")
    
    # Obtener primer usuario
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
    print_info("Este script creará datos de prueba y ejecutará pruebas de paginación")
    
    try:
        # Verificar conexión
        print_info("Verificando conexión con el servidor...")
        response = requests.get(f"{BASE_URL}/products")
        print_success("Conexión exitosa con el servidor")
    except Exception as e:
        print_error(f"No se puede conectar al servidor: {str(e)}")
        print_error("Asegúrate de que la aplicación Spring Boot esté ejecutándose en http://localhost:8080")
        return
    
    # Crear datos
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
    
    # Ejecutar pruebas
    time.sleep(1)  # Pausa antes de las pruebas
    
    test_pagination_basic()
    test_pagination_sorting()
    test_pagination_filters()
    test_pagination_slice()
    test_user_products()
    
    print_header("PRUEBAS COMPLETADAS")
    print_success(f"Se crearon {len(product_ids)} productos exitosamente")
    print_success("Todas las pruebas de paginación fueron ejecutadas")
    print_info("\nPuedes probar manualmente con:")
    print("  - http://localhost:8080/api/products?page=0&size=10")
    print("  - http://localhost:8080/api/products/slice?page=0&size=10")
    print("  - http://localhost:8080/api/products/search?name=laptop&page=0&size=5")

if __name__ == "__main__":
    main()

