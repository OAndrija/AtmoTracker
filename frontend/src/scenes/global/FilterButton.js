import { Box, Typography, useTheme } from "@mui/material";
import { useContext } from "react";
import { ColorModeContext, tokens } from "../../theme";

const FilterButton = ({ icon, text }) => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const colorMode = useContext(ColorModeContext);

    return (
        <Box 
            display="flex" 
            alignItems="center" 
            backgroundColor={colors.primary[400]} 
            borderRadius="35px" 
            sx={{ 
                ml: 1.5,
                pr: 1, 
                width: 'auto', 
                height: '30px', 
                boxShadow: '0px 4px 10px rgba(0,0,0, 0.2)',
                '&:hover': {
                    backgroundColor: colors.primary[900],
                    color: theme.palette.text.primary,
                },
                transition: 'background-color 0.3s, color 0.3s',
                cursor: 'pointer',
                padding: '0 10px'
            }}
        >
            {icon}
            <Typography fontSize={13} fontWeight='bold' sx={{ ml: 0.5, pr: 1, color: 'inherit'}}>
                {text}
            </Typography>
        </Box>
    );
};

export default FilterButton;
