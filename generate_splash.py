from PIL import Image, ImageDraw, ImageFont
import os

text = 'Routine\nManager'
font_size = 100
canvas_size = 1024

try:
    font = ImageFont.truetype('arialbd.ttf', font_size)
except IOError:
    font = ImageFont.load_default()

# Create Light mode image (black text)
img_light = Image.new('RGBA', (canvas_size, canvas_size), (0, 0, 0, 0))
draw_light = ImageDraw.Draw(img_light)

# We use align='center' for multiline text
bbox = draw_light.multiline_textbbox((0, 0), text, font=font, align='center')
text_w = bbox[2] - bbox[0]
text_h = bbox[3] - bbox[1]
x = (canvas_size - text_w) / 2
y = (canvas_size - text_h) / 2
draw_light.multiline_text((x, y), text, font=font, fill='black', align='center')
img_light.save('app/src/main/res/drawable/splash_text.png')

# Create Dark mode image (white text)
img_dark = Image.new('RGBA', (canvas_size, canvas_size), (0, 0, 0, 0))
draw_dark = ImageDraw.Draw(img_dark)
draw_dark.multiline_text((x, y), text, font=font, fill='white', align='center')
img_dark.save('app/src/main/res/drawable/splash_text_dark.png')

print('Generated bold multiline splash screens successfully.')
