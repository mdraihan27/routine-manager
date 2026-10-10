from PIL import Image
import PIL.ImageOps

img = Image.open('app/src/main/res/drawable/splash_text.png')
r, g, b, a = img.split()
rgb_image = Image.merge('RGB', (r, g, b))
inverted_image = PIL.ImageOps.invert(rgb_image)
r2, g2, b2 = inverted_image.split()
final_transparent_image = Image.merge('RGBA', (r2, g2, b2, a))
final_transparent_image.save('app/src/main/res/drawable/splash_text_dark.png')
print("Image inverted successfully")
