#include <iostream>
#include <chrono>
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

// Classe pour manipuler une image en mémoire CPU
class CPUBitmap {
public:
    CPUBitmap(int x, int y);
    ~CPUBitmap();
    unsigned char *get_ptr() const;

private:
    int DIMX;
    int DIMY;
    unsigned char *image;
};

// Constructeur de la classe CPUBitmap
CPUBitmap::CPUBitmap(int x, int y) : DIMX(x), DIMY(y) {
    image = new unsigned char[4 * DIMX * DIMY];
}

// Destructeur de la classe CPUBitmap
CPUBitmap::~CPUBitmap() {
    delete[] image;
}

// Méthode pour obtenir un pointeur vers l'image
unsigned char *CPUBitmap::get_ptr() const {
    return image;
}

// Fonction pour sauvegarder une image au format PNG
void save_image(const char *filename, const unsigned char *image, int width, int height) {
    stbi_write_png(filename, width, height, 4, image, width * 4);
}

// Structure pour les nombres complexes
struct cuComplex {
    float r;
    float i;
    cuComplex(float a, float b) : r(a), i(b) {}
    float magnitude2(void) { return r * r + i * i; }
    cuComplex operator*(const cuComplex &a) {
        return cuComplex(r * a.r - i * a.i, i * a.r + r * a.i);
    }
    cuComplex operator+(const cuComplex &a) {
        return cuComplex(r + a.r, i + a.i);
    }
};

// Fonction pour calculer la fractale de Julia
int julia(int x, int y, int DIM) {
    const float scale = 1.5;
    float jx = scale * (float)(DIM / 2 - x) / (DIM / 2);
    float jy = scale * (float)(DIM / 2 - y) / (DIM / 2);
    cuComplex c(0.3, 0.5); // Valeurs à modifier pour changer la forme de la fractale
    cuComplex a(jx, jy);
    int i = 0;
    for (i = 0; i < 200; i++) {
        a = a * a + c;
        if (a.magnitude2() > 1000)
            return i;
    }
    return i;
}

// Fonction pour calculer la fractale de Julia pour chaque pixel
void kernel(unsigned char *ptr, int DIM) {
    for (int y = 0; y < DIM; y++) {
        for (int x = 0; x < DIM; x++) {
            int offset = x + y * DIM;
            int iterations = julia(x, y, DIM);

            int red = (iterations * iterations) % 256;
            int green = iterations % 256;
            int blue = iterations % 256;

            ptr[offset * 4 + 0] = red;
            ptr[offset * 4 + 1] = green;
            ptr[offset * 4 + 2] = blue;
            ptr[offset * 4 + 3] = 255;
        }
    }
}

int main() {
    // Demandez à l'utilisateur de saisir la valeur de DIM
    int DIM; // Résolution de l'image
    std::cout << "Veuillez saisir la valeur de la résolution de l'image : ";
    std::cin >> DIM;

    // Chronométrage du temps d'exécution
    auto start_time = std::chrono::high_resolution_clock::now();

    // Création de l'objet CPUBitmap pour stocker l'image
    CPUBitmap bitmap(DIM, DIM);
    unsigned char *ptr = bitmap.get_ptr();

    // Calcul de la fractale de Julia pour chaque pixel
    kernel(ptr, DIM);

    // Sauvegarde de l'image générée au format PNG
    save_image("julia.png", ptr, DIM, DIM);

    // Fin du chronométrage et calcul de la durée d'exécution
    auto end_time = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end_time - start_time).count();

    // Affichage de la confirmation de sauvegarde et du temps total d'exécution du programme
    std::cout << "Image terminée !" << std::endl;
    std::cout << "Temps d'exécution : " << duration << " ms" << std::endl;
    std::cout << "Temps d'exécution en minutes : " << duration / 60000 << " min " << (duration % 60000) / 1000 << " s" << std::endl;

    return 0;
}

//ESIEA - 4A - Thomas COSSET - Jean-Juc LAURENT - Théo BACHELERY