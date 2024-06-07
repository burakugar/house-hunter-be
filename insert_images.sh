#!/bin/bash

# Set the path to the folder containing the image files
image_folder="Houses-dataset"

# Set the output folder for the generated image files and SQL script
output_folder="images1"

# Set the output file for the SQL script
output_file="$output_folder/generated_image_inserts.sql"

# Set the number of images per property
images_per_property=4

# Create the output folder if it doesn't exist
mkdir -p "$output_folder"

# Initialize an empty array to store the image file names
image_files=()

# Read all the image file names from the specified folder into the array
while IFS= read -r -d $'\0'; do
    image_files+=("$REPLY")
done < <(find "$image_folder" -type f -print0)

# Get the total number of image files
total_images=${#image_files[@]}

# Initialize the SQL script with the INSERT INTO statement
echo "INSERT INTO images (id, created_at, file_name, property_id) VALUES" > "$output_file"

# Initialize a counter for the image files
image_counter=0

# Loop through the property IDs and generate image files and SQL statements for each property
for ((i=100; i<=182; i++)); do
    property_id="123e4567-e89b-12d3-a456-426614174$i"

    # Loop through the number of images per property
    for ((j=0; j<$images_per_property; j++)); do
        # Get the image file name from the array
        image_file="${image_files[$image_counter]}"

        # Extract the image file name without the path
        image_name=$(basename "$image_file")

        # Convert the image name to lowercase
        lowercase_image_name=$(echo "$image_name" | tr '[:upper:]' '[:lower:]')

        # Generate the UUID in lowercase for the image file name
        lowercase_uuid=$(uuidgen | tr '[:upper:]' '[:lower:]')

        # Generate the new image file name with lowercase UUID
        new_image_name="${lowercase_uuid}_$lowercase_image_name"

        # Copy the image file to the output folder with the new name
        cp "$image_file" "$output_folder/$new_image_name"

        # Generate the UUID in lowercase for the image ID
        lowercase_id=$(uuidgen | tr '[:upper:]' '[:lower:]')

        # Get the current timestamp in the desired format
        current_timestamp=$(date -u +"%Y-%m-%d %H:%M:%S.%6N" | sed 's/\..*N$/&00000/')

        # Generate the SQL statement for the current image and property
        echo "    ('$lowercase_id', '$current_timestamp', '$new_image_name', '$property_id')," >> "$output_file"

        # Increment the image counter
        image_counter=$((image_counter + 1))

        # Break the loop if all image files have been processed
        if [ $image_counter -ge $total_images ]; then
            break 2
        fi
    done
done

# Remove the trailing comma from the last SQL statement
sed -i '' '$ s/,$/;/' "$output_file"

echo "Image files and SQL script generated successfully in the '$output_folder' folder."
