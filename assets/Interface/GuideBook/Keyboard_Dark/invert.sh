#!/bin/bash

# Directory containing images
DIRECTORY="."

# Loop over all image files in the directory
for FILE in "$DIRECTORY"/*; do
  # Check if the file is a regular file (not a directory)
  if [ -f "$FILE" ]; then
    # Invert the image and replace the original
    convert "$FILE" -negate "$FILE"
    echo "Inverted $FILE"
  fi
done
